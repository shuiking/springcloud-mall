package com.cloud.mall.seckill.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("cloud-coupon")
public interface CouponFeignService {
    @RequestMapping("/cloudcoupon/seckillsession/getSeckillSessionsIn3Days")
    R getSeckillSessionsIn3Days();
}
