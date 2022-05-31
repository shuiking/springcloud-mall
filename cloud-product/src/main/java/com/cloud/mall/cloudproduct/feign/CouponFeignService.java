package com.cloud.mall.cloudproduct.feign;

import com.cloud.common.to.SkuReductionTo;
import com.cloud.common.to.SpuBoundTo;
import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("cloud-coupon")
public interface CouponFeignService{
    @PostMapping("/cloudcoupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);
    @PostMapping("/cloudcoupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
