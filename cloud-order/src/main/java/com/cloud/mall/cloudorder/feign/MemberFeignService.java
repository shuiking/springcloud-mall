package com.cloud.mall.cloudorder.feign;


import com.cloud.mall.cloudorder.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("cloud-member")
public interface MemberFeignService {
    @RequestMapping("/cloudmember/memberreceiveaddress/getAddressByUserId")
    List<MemberAddressVo> getAddressByUserId(@RequestBody Long userId);
}
