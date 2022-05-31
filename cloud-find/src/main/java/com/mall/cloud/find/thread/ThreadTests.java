package com.mall.cloud.find.thread;

import java.util.concurrent.*;

public class ThreadTests {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // System.out.println("main......start.....");
        // Thread thread = new Thread01();
        // thread.start();
        // System.out.println("main......end.....");

        // Runable01 runable01 = new Runable01();
        // new Thread(runable01).start();

        // FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        // new Thread(futureTask).start();
        // System.out.println(futureTask.get());

        // service.execute(new Runable01());
        // Future<Integer> submit = service.submit(new Callable01());
        // submit.get();

        System.out.println("main......start.....");
        // CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        //     System.out.println("当前线程：" + Thread.currentThread().getId());
        //     int i = 10 / 2;
        //     System.out.println("运行结果：" + i);
        // }, executor);

        /**
         * 方法完成后的处理
         */
        // CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
        //     System.out.println("当前线程：" + Thread.currentThread().getId());
        //     int i = 10 / 0;
        //     System.out.println("运行结果：" + i);
        //     return i;
        // }, executor).whenComplete((res,exception) -> {
        //     //虽然能得到异常信息，但是没法修改返回数据
        //     System.out.println("异步任务成功完成了...结果是：" + res + "异常是：" + exception);
        // }).exceptionally(throwable -> {
        //     //可以感知异常，同时返回默认值
        //     return 10;
        // });

        /**
         * 方法执行完后端处理
         */
        // CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
        //     System.out.println("当前线程：" + Thread.currentThread().getId());
        //     int i = 10 / 2;
        //     System.out.println("运行结果：" + i);
        //     return i;
        // }, executor).handle((result,thr) -> {
        //     if (result != null) {
        //         return result * 2;
        //     }
        //     if (thr != null) {
        //         System.out.println("异步任务成功完成了...结果是：" + result + "异常是：" + thr);
        //         return 0;
        //     }
        //     return 0;
        // });


        /**
         * 线程串行化
         * 1、thenRunL：不能获取上一步的执行结果
         * 2、thenAcceptAsync：能接受上一步结果，但是无返回值
         * 3、thenApplyAsync：能接受上一步结果，有返回值
         *
         */
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).thenApplyAsync(res -> {
            System.out.println("任务2启动了..." + res);
            return "Hello" + res;
        }, executor);
        System.out.println("main......end....." + future.get());

    }

    private static void threadPool() {
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
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()

        );

//        Executors.newCachedThreadPool() core为0 都可回收
//        Executors.newFixedThreadPool() 固定大小 core=max 都不可以回收
//        Executors.newScheduledThreadPool() 定时任务的线程池
//        Executors.newSingleThreadExecutor() 单线程的线程池 后台从队列获取任务，逐个执行
//        //定时任务的线程池
//        ExecutorService service = Executors.newScheduledThreadPool(2);
    }


    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Runable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

}
