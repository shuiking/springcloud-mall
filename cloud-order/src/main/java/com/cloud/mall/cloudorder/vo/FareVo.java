package com.cloud.mall.cloudorder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;
}
