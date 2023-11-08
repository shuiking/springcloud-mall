package com.cloud.mall.cart.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("cloud-product")
public interface ProductFeignService {
    @RequestMapping("/cloudproduct/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @GetMapping(value = "/cloudproduct/skusaleattrvalue/stringList/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping(value = "/cloudproduct/skuinfo/{skuId}/price")
    R getPrice(@PathVariable("skuId") Long skuId);
}
