package com.cloud.mall.cloudware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.cloud.common.exception.BizCodeEnum;
import com.cloud.common.exception.NoStockException;
import com.cloud.mall.cloudware.vo.SkuHasStockVo;
import com.cloud.mall.cloudware.vo.WareSkuLockVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.cloudware.entity.WareSkuEntity;
import com.cloud.mall.cloudware.service.WareSkuService;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;



/**
 * 商品库存
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:47:34
 */
@RestController
@RequestMapping("cloudware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 下订单时锁库存
     * @param
     * @return
     */
    @RequestMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo lockVo) {
        try {
            Boolean lock = wareSkuService.orderLockStock(lockVo);
            return R.ok().setData(lock);
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMessage());
        }
    }


    //检查库存
    @PostMapping("/hasstock")
    public List<SkuHasStockVo> getSkusHasStock(@RequestBody List<Long> skuIds)
    {
        return wareSkuService.getSkusHasStock(skuIds);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cloudware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cloudware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cloudware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cloudware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cloudware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
