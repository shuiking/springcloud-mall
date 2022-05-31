package com.cloud.mall.cloudorder.dao;

import com.cloud.mall.cloudorder.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:43:23
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
