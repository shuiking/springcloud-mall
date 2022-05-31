package com.cloud.auth.feign;

import com.cloud.auth.vo.MemberLoginVo;
import com.cloud.auth.vo.MemberRegisterVo;
import com.cloud.auth.vo.SocialUser;
import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cloud-member")
public interface MemberFeignService {

    @RequestMapping("/cloudmember/member/register")
    R register(@RequestBody MemberRegisterVo registerVo);

    @PostMapping(value = "/cloudmember/member/login")
    R login(@RequestBody MemberLoginVo vo);

    @PostMapping(value = "/cloudmember/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);

    @PostMapping(value = "/cloudmember/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;
}
