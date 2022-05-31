package com.mall.cloud.find.feign;

import com.cloud.common.utils.R;;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("cloud-product")
public interface ProductFeignService {
    @GetMapping("/cloudproduct/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);
}
