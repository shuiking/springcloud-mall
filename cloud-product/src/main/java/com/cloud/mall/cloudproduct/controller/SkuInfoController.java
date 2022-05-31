package com.cloud.mall.cloudproduct.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.cloud.mall.cloudproduct.vo.SpuSaveVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.cloudproduct.entity.SkuInfoEntity;
import com.cloud.mall.cloudproduct.service.SkuInfoService;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;



/**
 * sku信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 15:09:49
 */
@RestController
@RequestMapping("cloudproduct/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping(value = "/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId) {

        //获取当前商品的信息
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        //获取商品的价格
        BigDecimal price = skuInfo.getPrice();

        return R.ok().setData(price.toString());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cloudproduct:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    @RequiresPermissions("cloudproduct:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cloudproduct:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
        skuInfoService.save(skuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cloudproduct:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cloudproduct:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
