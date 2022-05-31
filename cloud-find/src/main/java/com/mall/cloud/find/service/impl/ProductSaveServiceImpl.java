package com.mall.cloud.find.service.impl;

import com.alibaba.fastjson.JSON;
import com.cloud.common.to.es.SkuEsModel;
import com.mall.cloud.find.config.ElasticConfig;
import com.mall.cloud.find.constant.EsConstant;
import com.mall.cloud.find.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es中
        //1.给es建立索引 product 建立好映射关系
        //2.es保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for(SkuEsModel model:skuEsModels)
        {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticConfig.COMMON_OPTIONS);

        //TODO 批量处理错误
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.error("商品上架错误:{}",collect);
        return b;
    }
}
