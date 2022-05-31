package com.cloud.mall.cloudware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
