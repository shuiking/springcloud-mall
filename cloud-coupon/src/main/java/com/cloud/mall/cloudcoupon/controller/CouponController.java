package com.cloud.mall.cloudcoupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.cloud.common.to.SpuBoundTo;
import com.cloud.mall.cloudcoupon.entity.SpuBoundsEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.cloudcoupon.entity.CouponEntity;
import com.cloud.mall.cloudcoupon.service.CouponService;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;



/**
 * 优惠券信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:25:45
 */
@RefreshScope
@RestController
@RequestMapping("cloudcoupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @Value("${coupon.user.name}")
    private String name;
    @Value("${coupon.user.age}")
    private String age;

    @RequestMapping("/test")
    public R test() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCouponName("1个瓜3元，3个瓜10元");
        return R.ok().put("name",name).put("age",age);
    }

    @RequestMapping("/member/list")
    public R memberCoupon() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCouponName("1个瓜3元，3个瓜10元");
        return R.ok().put("coupons",Arrays.asList(coupon));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cloudcoupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("cloudcoupon:coupon:info")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("cloudcoupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("cloudcoupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("cloudcoupon:coupon:delete")
    public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
