package com.cloud.mall.cloudproduct.dao;

import com.cloud.mall.cloudproduct.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.mall.cloudproduct.vo.SkuItemVo;
import com.cloud.mall.cloudproduct.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 15:09:49
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
