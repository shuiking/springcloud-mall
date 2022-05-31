package com.cloud.mall.cloudorder.feign;

import com.cloud.mall.cloudorder.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("cloud-cart")
public interface CartFeignService {
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    List<OrderItemVo> getCurrentCartItems();
}
