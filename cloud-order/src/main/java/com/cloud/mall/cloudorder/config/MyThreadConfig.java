package com.cloud.mall.cloudorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool)
    {
        /**
         * 7大参数
         * 1.corePoolSize 核心线程数（一直存在除非allowCoreThreadTimeOut） 创建好久准备好
         * 2.maximumPoolSize 最大线程 控制资源 最大200
         * 3.keepAliveTime 最大存活时间 如果当前线程数量大于核心线程corePoolSize的数量
         *                 释放空闲线程 只要大于keepAliveTime的时间就释放除核心线程之外的线程
         * 4.TimeUnit 时间单位
         * 5.BlockingQueue<Runnable> 阻塞队列 等待空余的线程来处理任务
         * 6.ThreadFactory 线程创建工厂
         * 7.RejectedExecutionHandler 如果队列满了就按照自己设置的拒绝策略执行
         *
         *
         * 工作顺序
         * 1.core线程创建，接受任务
         * 2.core满了，先放队列，等待空闲线程、阻塞队列满了，就开新的线程执行，最大开大max200
         * 3.max满了执行拒绝策略
         * 4.max都执行完成等待keepAliveTime以后释放（max-core）的线程
         */
        return new ThreadPoolExecutor(
                pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
