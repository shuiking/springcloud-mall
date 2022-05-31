package com.cloud.mall.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudmember.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:37:18
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

