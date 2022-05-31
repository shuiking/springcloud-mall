package com.cloud.mall.cloudmember;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ATest {
    @Test
    public void test1(){
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }
}
