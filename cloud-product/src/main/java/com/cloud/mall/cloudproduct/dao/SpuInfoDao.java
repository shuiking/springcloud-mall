package com.cloud.mall.cloudproduct.dao;

import com.cloud.common.constant.ProductConstant;
import com.cloud.mall.cloudproduct.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 15:09:49
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId,@Param("code") int code);
}
