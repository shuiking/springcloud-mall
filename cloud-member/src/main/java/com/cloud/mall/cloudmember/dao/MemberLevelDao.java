package com.cloud.mall.cloudmember.dao;

import com.cloud.mall.cloudmember.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:37:18
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefaultLevel();
}
