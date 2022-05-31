package com.cloud.mall.cloudproduct.web;

import com.cloud.mall.cloudproduct.entity.CategoryEntity;
import com.cloud.mall.cloudproduct.service.CategoryService;
import com.cloud.mall.cloudproduct.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;


    @GetMapping({"/","/index.html"})
    public String IndexPage(Model model)
    {
        //TODO1
        List<CategoryEntity>categoryEntities=categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCataogJson()
    {
        Map<String,List<Catelog2Vo>> map=categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello()
    {
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务....."+Thread.currentThread().getId());
            Thread.sleep(300);
        }catch (Exception e){
        }
        finally
        {
            System.out.println("释放锁...."+Thread.currentThread().getId());
        }
        return "hello";
    }

}
