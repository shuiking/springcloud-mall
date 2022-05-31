package com.cloud.mall.cloudproduct.feign;

import com.cloud.common.utils.R;
import com.cloud.mall.cloudproduct.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("cloud-ware")
public interface WareFeignService {
    @PostMapping("/cloudware/waresku/hasstock")
    List<SkuHasStockVo> getSkusHasStock(@RequestBody List<Long> skuIds);
}
