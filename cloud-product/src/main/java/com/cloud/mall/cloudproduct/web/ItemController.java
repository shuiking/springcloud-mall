package com.cloud.mall.cloudproduct.web;

import com.cloud.mall.cloudproduct.service.SkuInfoService;
import com.cloud.mall.cloudproduct.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;
    //展示sku详情
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo=skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
