package com.cloud.mall.cloudorder.feign;

import com.cloud.common.utils.R;
import com.cloud.mall.cloudorder.vo.SkuHasStockVo;
import com.cloud.mall.cloudorder.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("cloud-ware")
public interface WareFeignService {
    //检查库存
    @PostMapping("/cloudware/waresku/hasstock")
    List<SkuHasStockVo> getSkusHasStock(@RequestBody List<Long> skuIds);

    @GetMapping(value = "/cloudware/waresku/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @RequestMapping("/cloudware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo lockVo);
}
