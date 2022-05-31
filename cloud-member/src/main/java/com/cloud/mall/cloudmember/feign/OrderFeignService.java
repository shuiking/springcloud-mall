package com.cloud.mall.cloudmember.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("cloud-order")
public interface OrderFeignService {
    @PostMapping("/cloudorder/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
