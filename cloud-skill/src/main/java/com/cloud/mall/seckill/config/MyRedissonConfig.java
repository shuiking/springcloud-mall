package com.cloud.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {
    //所有对redisson的使用都是通过RedissonClient
    @Bean
    public RedissonClient redissonClient(){
        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        //根据config创建实例RedissonClient对象
        return Redisson.create(config);
    }
}
