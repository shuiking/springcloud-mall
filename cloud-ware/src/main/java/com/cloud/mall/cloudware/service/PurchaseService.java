package com.cloud.mall.cloudware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudware.entity.PurchaseEntity;
import com.cloud.mall.cloudware.vo.MergeVo;
import com.cloud.mall.cloudware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:47:34
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void receive(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}

