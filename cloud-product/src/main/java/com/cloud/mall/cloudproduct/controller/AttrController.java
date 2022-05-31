package com.cloud.mall.cloudproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.cloud.mall.cloudproduct.entity.ProductAttrValueEntity;
import com.cloud.mall.cloudproduct.service.ProductAttrValueService;
import com.cloud.mall.cloudproduct.vo.AttrRespVo;
import com.cloud.mall.cloudproduct.vo.AttrVo;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.cloudproduct.entity.AttrEntity;
import com.cloud.mall.cloudproduct.service.AttrService;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;



/**
 * 商品属性
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 15:09:49
 */
@RestController
@RequestMapping("cloudproduct/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;


    /**
     * 列表
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baselistAttrlistforspu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> valueEntities=productAttrValueService.baselistAttrlistforspu(spuId);

        return R.ok().put("data",valueEntities);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType")String type){
        PageUtils page= attrService.queryBaseAttrPage(params, catelogId,type);
        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cloudproduct:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    @RequiresPermissions("cloudproduct:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cloudproduct:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@RequestBody List<ProductAttrValueEntity> entities,
                           @PathVariable("spuId") Long spuId){
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cloudproduct:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cloudproduct:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
