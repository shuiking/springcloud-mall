package com.cloud.mall.cloudorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudorder.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:43:23
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

