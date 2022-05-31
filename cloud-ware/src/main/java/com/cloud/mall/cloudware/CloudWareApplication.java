package com.cloud.mall.cloudware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
public class CloudWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudWareApplication.class, args);
    }

}
