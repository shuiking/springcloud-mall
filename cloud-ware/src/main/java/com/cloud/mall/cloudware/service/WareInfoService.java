package com.cloud.mall.cloudware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudware.entity.WareInfoEntity;
import com.cloud.mall.cloudware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:47:34
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);

}

