package com.cloud.mall.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.cloudmember.entity.MemberEntity;
import com.cloud.mall.cloudmember.exception.PhoneNumExistException;
import com.cloud.mall.cloudmember.exception.UserExistException;
import com.cloud.mall.cloudmember.vo.MemberLoginVo;
import com.cloud.mall.cloudmember.vo.MemberRegisterVo;
import com.cloud.mall.cloudmember.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author k
 * @email 2261933439@qq.com
 * @date 2022-05-30 19:37:18
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo registerVo);

    void checkUserNameUnique(String userName) throws UserExistException;

    void checkPhoneUnique(String phone) throws PhoneNumExistException;

    //用户登录
    MemberEntity login(MemberLoginVo vo);

    //社交用户的登录
    MemberEntity login(SocialUser socialUser) throws Exception;

    //微信登录
    MemberEntity login(String accessTokenInfo);

}

