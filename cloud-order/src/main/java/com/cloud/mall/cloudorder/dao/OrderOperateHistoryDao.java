package com.cloud.mall.cloudorder.dao;

import com.cloud.mall.cloudorder.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:43:23
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
