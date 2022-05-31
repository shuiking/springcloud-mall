package com.cloud.mall.cloudproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.cloud.common.constant.ProductConstant;
import com.cloud.common.to.SkuReductionTo;
import com.cloud.common.to.SpuBoundTo;
import com.cloud.common.to.es.SkuEsModel;
import com.cloud.common.utils.R;
import com.cloud.mall.cloudproduct.entity.*;
import com.cloud.mall.cloudproduct.feign.CouponFeignService;
import com.cloud.mall.cloudproduct.feign.SearchFeignService;
import com.cloud.mall.cloudproduct.feign.WareFeignService;
import com.cloud.mall.cloudproduct.service.*;
import com.cloud.mall.cloudproduct.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.cloudproduct.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.保存spu基本信息 spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2.保存spu的描述图片 spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.保存spu的图片集 spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4.保存spu的规格参数 attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setAttrId(attr.getAttrId());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //5.保存spu的积分信息
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r=couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }

        //6.保存spu的sku信息
        List<Skus> skus = vo.getSkus();
        if(skus!=null&&skus.size()>0){
            skus.forEach(item->{
                String defaultImg="";
                for(Images img:item.getImages()){
                    if(img.getDefaultImg()==1){
                        defaultImg=img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                //6.1）、sku的基本信息；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();

                //6.2）、sku的图片信息；pms_sku_image
                List<SkuImagesEntity> imagesEntities= item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //TODO 没有图片路径的无需保存
                    //true为保存 false为过滤
                    return !StringUtils.isEmpty(entity.getImgUrl());
                        }
                ).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                //6.3）、sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> entityList = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuSaleAttrValueEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(entityList);

                //6.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                    R r1=couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode()!=0){
                        log.error("远程保存spu优惠信息失败");
                    }
                }


            });
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(i -> i.eq("id", key).or().like("spu_name", key));
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !brandId.equals("0")) {
            wrapper.eq("brand_id", brandId);
        }
        String catalogId = (String) params.get("catalogId");
        if (StringUtils.isNotEmpty(catalogId) && !catalogId.equals("0")) {
            wrapper.eq("catalog_id", catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 1.查询spu的所有baseattr   pms_product_attr_value （此表保存spuid与baseattrid关联关系）
        List<ProductAttrValueEntity> baseAttrs =
                productAttrValueService.baselistAttrlistforspu(spuId);
        // 1.1抽取baseAttrId
        List<Long> attrIds = baseAttrs.stream().
                map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 1.2查询当前 sku 的所有可被检索的【基本属性】（规格参数） pms_attr
        List<Long> finalAttrIds = attrService.selectSearchAttrs(attrIds);
        // 1.3过滤掉不可被检索的
        Set<Long> idSet = new HashSet<>(finalAttrIds);
        List<SkuEsModel.Attrs> finalAttrs = baseAttrs.stream().
                filter(item -> idSet.contains(item.getAttrId())).
                map(item -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(Collectors.toList());

        // 2.查出当前 spuId 对应的商品信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().
                map(SkuInfoEntity::getSkuId).
                collect(Collectors.toList());
        // 2.1 查询当前 sku 的所有可被检索的【销售属性】
        List<SkuSaleAttrValueEntity> saleAttrs = skuSaleAttrValueService.getSaleAttrsBatch(skuIdList);

        // 3.查询库存信息
        Map<Long, Boolean> map = null;
        try {
            List<Long> longList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            List<SkuHasStockVo> skuHasStocks = wareFeignService.getSkusHasStock(longList);
            map = skuHasStocks.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }catch (Exception e){
            log.error("远程调用库存服务失败,原因{}",e);
        }

        // 4. 封装每个 sku 信息
        Map<Long, Boolean> finalMap = map;
        List<SkuEsModel> collect = skus.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO 2、热度评分。0
            skuEsModel.setHotScore(0L);
            //TODO 3、查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());
            //设置可搜索属性
            skuEsModel.setAttrs(finalAttrs);
            //设置是否有库存
            skuEsModel.setHasStock(finalMap==null?false:finalMap.get(sku.getSkuId()));
            return skuEsModel;
        }).collect(Collectors.toList());

        // 5. 发送数据给 es
        log.error("this is debug item:" + JSON.toJSONString(collect));
        R result = searchFeignService.productStatusUp(collect);
        if (result.getCode() == 0) {
            // 远程调用成功
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败
            // TODO 重复调用\接口幂等性\重试机制
            log.error("远程调用失败");
            throw new RuntimeException("远程调用失败");
        }
    }

    @Override
    public SpuInfoEntity getSpuBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        SpuInfoEntity spu = this.getById(skuInfoEntity.getSpuId());
        BrandEntity brandEntity = brandService.getById(spu.getBrandId());
        spu.setBrandName(brandEntity.getName());
        return spu;
    }


}