package com.cloud.mall.cloudproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudproduct.entity.SpuInfoDescEntity;
import com.cloud.mall.cloudproduct.entity.SpuInfoEntity;
import com.cloud.mall.cloudproduct.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 15:09:49
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    //商品的上架
    void up(Long spuId);

    SpuInfoEntity getSpuBySkuId(Long skuId);
}

