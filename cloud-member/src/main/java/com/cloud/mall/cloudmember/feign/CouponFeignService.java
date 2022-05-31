package com.cloud.mall.cloudmember.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

//远程调用接口
@FeignClient("cloud-coupon")
public interface CouponFeignService {
    @RequestMapping("/cloudcoupon/coupon/member/list")
    R memberCoupon();
}
