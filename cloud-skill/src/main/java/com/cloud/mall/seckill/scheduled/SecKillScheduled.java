//package com.cloud.mall.seckill.scheduled;
//
//import com.cloud.mall.seckill.service.SeckillService;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class SecKillScheduled {
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    @Autowired
//    private SeckillService seckillService;
//
//    //秒杀商品上架功能的锁
//    private final String upload_lock = "seckill:upload:lock";
//
//    /**
//     * 定时任务
//     * 每天三点上架最近三天的秒杀商品
//     */
//    @Async
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void uploadSeckillSkuLatest3Days() {
//        //为避免分布式情况下多服务同时上架的情况，使用分布式锁
//        RLock lock = redissonClient.getLock(upload_lock);
//        try {
//            //锁住当前的线程+释放时间
//            lock.lock(10, TimeUnit.SECONDS);
//            seckillService.uploadSeckillSkuLatest3Days();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            //解锁
//            lock.unlock();
//        }
//    }
//}
