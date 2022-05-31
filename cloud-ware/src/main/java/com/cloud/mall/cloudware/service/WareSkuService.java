package com.cloud.mall.cloudware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.to.mq.OrderTo;
import com.cloud.common.to.mq.StockLockedTo;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudware.entity.WareSkuEntity;
import com.cloud.mall.cloudware.vo.SkuHasStockVo;
import com.cloud.mall.cloudware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:47:34
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);
}

