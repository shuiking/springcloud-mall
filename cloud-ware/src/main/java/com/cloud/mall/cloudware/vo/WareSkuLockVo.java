package com.cloud.mall.cloudware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {
    private String OrderSn;

    private List<OrderItemVo> locks;
}
