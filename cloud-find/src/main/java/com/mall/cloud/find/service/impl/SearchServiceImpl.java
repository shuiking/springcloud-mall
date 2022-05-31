package com.mall.cloud.find.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.common.utils.R;
import com.mall.cloud.find.config.ElasticConfig;
import com.mall.cloud.find.constant.EsConstant;
import com.mall.cloud.find.feign.ProductFeignService;
import com.mall.cloud.find.service.SearchService;
import com.mall.cloud.find.vo.AttrResponseVo;
import com.mall.cloud.find.vo.SearchParam;
import com.mall.cloud.find.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("SearchService")
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    //es的搜索
    @Override
    public SearchResult getSearchResult(SearchParam searchParam)
    {
        //动态构建出查询需要的dsl语句
        SearchResult result=null;

        //准备请求
        SearchRequest searchRequest =buildSearchRequest(searchParam);
        try {
            //执行请求
            SearchResponse search = restHighLevelClient.search(searchRequest, ElasticConfig.COMMON_OPTIONS);

            //分析数据并封住为我们需要的格式
            result = buildSearchResult(search,searchParam);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    //结果封装
    private SearchResult buildSearchResult(SearchResponse response,SearchParam searchParam) {
        SearchResult result = new SearchResult();

        //返回查到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels=new ArrayList<>();
        if(hits.getHits()!=null&&hits.getHits().length>0)
        {
            for(SearchHit hit:hits.getHits())
            {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                //设置高亮属性
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highLight = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(highLight);
                }
                esModels.add(esModel);
            }
            result.setProduct(esModels);
        }



        //查询结果涉及到的品牌
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        Aggregations aggregations = response.getAggregations();
        //ParsedLongTerms用于接收terms聚合的结果，并且可以把key转化为Long类型的数据
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
            for (Terms.Bucket bucket : brandAgg.getBuckets()) {
                // 得到品牌id
                Long brandId = bucket.getKeyAsNumber().longValue();

                Aggregations subBrandAggs = bucket.getAggregations();
                // 得到品牌图片
                ParsedStringTerms brandImgAgg = subBrandAggs.get("brand_img_agg");
                String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
                // 得到品牌名字
                Terms brandNameAgg = subBrandAggs.get("brand_name_agg");
                String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo(brandId, brandName, brandImg);
                brandVos.add(brandVo);
            }
            result.setBrands(brandVos);

        //查询涉及到的所有分类
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = aggregations.get("catalog_agg");
            for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
                // 获取分类id
                Long catalogId = bucket.getKeyAsNumber().longValue();
                Aggregations subcatalogAggs = bucket.getAggregations();
                // 获取分类名
                ParsedStringTerms catalogNameAgg = subcatalogAggs.get("catalog_name_agg");
                String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo(catalogId, catalogName);
                catalogVos.add(catalogVo);
            }
            result.setCatalogs(catalogVos);

        //2、当前商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        //获取属性信息的聚合
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //2、得到属性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3、得到属性的所有值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 封装分页信息
        // 当前页码
        result.setPageNum(searchParam.getPageNum());
        // 总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        // 总页码
        int totalPages=total%EsConstant.PRODUCT_PAGE_SIZE==0?((int)total/EsConstant.PRODUCT_PAGE_SIZE):
                ((int)total/EsConstant.PRODUCT_PAGE_SIZE+1);
        result.setTotalPages(totalPages);


        //构建面包屑导航
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = searchParam.getAttrs().stream().map(attr -> {
                //分析每一个attrs传过来的参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                //取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                //拿到所有的查询条件，去掉当前
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr,"UTF-8");
                    encode.replace("+","%20");  //浏览器对空格的编码和Java不一样，差异化处理
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = searchParam.get_queryString().replace("&attrs=" + attr, "");
                navVo.setLink("http://search.cloudmall.com:9002/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }



        return result;
    }

    //请求封装
    private SearchRequest buildSearchRequest(SearchParam searchParam)
    {
        //构建dsl语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //1、模糊匹配 过滤
        //query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //must 模糊匹配
        if(!StringUtils.isEmpty(searchParam.getKeyword()))
        {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }

        //bool filter 过滤 按照3级分类的id查
        if(searchParam.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }

        //bool filter 过滤 按照品牌的id查
        if (searchParam.getBrandId()!=null&&searchParam.getBrandId().size()>0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }

        //attr 过滤
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){

            searchParam.getAttrs().forEach(item -> {
                //attrs=1_5寸:8寸&2_16G:8G
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


                //attrs=1_5寸:8寸
                String[] s = item.split("_");
                String attrId=s[0];
                String[] attrValues = s[1].split(":");//这个属性检索用的值
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            });

        }


        //bool filter 过滤 按照是否有库存查
        if (searchParam.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }

        //bool filter 过滤 按照价格区间查
        if(!StringUtils.isEmpty(searchParam.getSkuPrice()))
        {
            //1_500 _500 500_
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            String[] s=searchParam.getSkuPrice().split("_");
            if(s.length==2)
            {
                skuPrice.gte(s[0]).lte(s[1]);
            }else if(s.length==1)
            {
                if(searchParam.getSkuPrice().startsWith("_"))
                {
                    skuPrice.lte(s[0]);
                }
                if(searchParam.getSkuPrice().endsWith("_"))
                {
                    skuPrice.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(skuPrice);
        }

        //所有条件封装
        sourceBuilder.query(boolQueryBuilder);

        //2、排序 分页 高亮

        //排序
        if(!StringUtils.isEmpty(searchParam.getSort()))
        {
            //sort=hotScore_asc/desc
            String sort = searchParam.getSort();
            String[] s = sort.split("_");
            SortOrder order=s[1].equalsIgnoreCase("asc")?SortOrder.ASC: SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }

        //分页
        sourceBuilder.from((searchParam.getPageNum()-1)*EsConstant.PRODUCT_PAGE_SIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //高亮
        if(!StringUtils.isEmpty(searchParam.getKeyword()))
        {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        //3、聚合分析
        //brand聚合
        //TODO brand聚合的子聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword").size(50));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg.keyword").size(50));
        sourceBuilder.aggregation(brandAgg);

        //TODO catalog聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(50);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName.keyword").size(1));
        sourceBuilder.aggregation(catalogAgg);

        //TODO attrs聚合
        //2. 按照属性信息进行聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg","attrs");
        //2.1 按照属性ID进行聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_agg.subAggregation(attr_id_agg);
        //2.1.1 在每个属性ID下，按照属性名进行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName.keyword").size(1));
        //2.1.1 在每个属性ID下，按照属性值进行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue.keyword").size(50));
        sourceBuilder.aggregation(attr_agg);

        System.out.println(sourceBuilder.toString());


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

        return searchRequest;
    }
}
