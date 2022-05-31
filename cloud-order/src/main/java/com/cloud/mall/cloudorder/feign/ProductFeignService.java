package com.cloud.mall.cloudorder.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("cloud-product")
public interface ProductFeignService {
    @RequestMapping("/cloudproduct/spuinfo/skuId/{skuId}")
    R getSpuBySkuId(@PathVariable("skuId") Long skuId);

    @RequestMapping("/cloudproduct/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
