package com.cloud.mall.cloudcoupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class CloudCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudCouponApplication.class, args);
    }

}
