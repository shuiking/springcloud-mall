package com.cloud.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class gatewayMain {
    public static void main(String[] args) {
        SpringApplication.run(gatewayMain.class,args);
    }
}
