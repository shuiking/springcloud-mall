package com.cloud.mall.cloudware.feign;

import com.cloud.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("cloud-member")
public interface MemberFeignService {
    @RequestMapping("/cloudmember/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}
