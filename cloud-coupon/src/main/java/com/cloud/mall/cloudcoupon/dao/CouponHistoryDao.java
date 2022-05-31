package com.cloud.mall.cloudcoupon.dao;

import com.cloud.mall.cloudcoupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:25:45
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
