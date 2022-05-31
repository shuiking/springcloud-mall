package com.cloud.mall.cloudproduct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.mall.cloudproduct.service.CategoryBrandRelationService;
import com.cloud.mall.cloudproduct.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.cloudproduct.dao.CategoryDao;
import com.cloud.mall.cloudproduct.entity.CategoryEntity;
import com.cloud.mall.cloudproduct.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //2.组装成父子结构

        //2.找出所有的1级分类
        List<CategoryEntity> levelCategoryFirst = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildren(menu, categoryEntities));
                    return menu;
                }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
        return levelCategoryFirst;
    }

    @Override
    public void removeMenusByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单是否被其他地方调用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        //顺序反转
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    @Caching(evict = {
            @CacheEvict(value ="category",key = "'getLevel1Categorys'"),
            @CacheEvict(value ="category",key = "'getCatalogJson'")
    })
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //加入springboot封装的缓存方法 如果缓存中有就不调用 没有就调用方法并将结果放入缓存
    //缓存分区 按照业务的1类型分区
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value ="category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询数据库");
        //数据库查询变为1次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //查出所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();

        //封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查找当前一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //封装上面的结果
            List<Catelog2Vo> catelog2Vo = null;
            if (categoryEntities != null) {
                catelog2Vo = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo1 = new Catelog2Vo(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> level3 = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3 != null) {
                        List<Catelog2Vo.Catalog3Vo> catelog3Vos = level3.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getParentCid().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo1.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo1;
                }).collect(Collectors.toList());
            }
            return catelog2Vo;
        }));
        return parent_cid;
    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //注意锁的名字
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();

        Map<String,List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb=getDataFromDb();
        }finally {
            lock.unlock();
        }
        return dataFromDb;
    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //1.占分布式锁 去redis占坑
        //设置过期时间避免死锁 必须是和加锁同步的 原子性
        Boolean lock = ops.setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功.....");
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                //加锁成功 执行业务
                dataFromDb = getDataFromDb();
            } finally {
                //删除锁 原子操作 使用lua脚本
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return dataFromDb;

        } else {
            System.out.println("获取分布式锁失败.....");
            //加锁失败 重试 自旋
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }


    public Map<String, List<Catelog2Vo>> getDataFromDb() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //1、加入缓存逻辑
        String catalogJson = ops.get("catalogJson");

        if (!StringUtils.isEmpty(catalogJson)) {
            //缓存不为空直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询数据库");
        //数据库查询变为1次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //查出所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();

        //封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查找当前一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //封装上面的结果
            List<Catelog2Vo> catelog2Vo = null;
            if (categoryEntities != null) {
                catelog2Vo = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo1 = new Catelog2Vo(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> level3 = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3 != null) {
                        List<Catelog2Vo.Catalog3Vo> catelog3Vos = level3.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getParentCid().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo1.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo1;
                }).collect(Collectors.toList());
            }
            return catelog2Vo;
        }));
        String s = JSON.toJSONString(parent_cid);
        ops.set("catalogJson", s, 1, TimeUnit.DAYS);
        return parent_cid;

    }

    //从数据库查询封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

        //数据库查询变为1次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //查出所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();

        //封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查找当前一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //封装上面的结果
            List<Catelog2Vo> catelog2Vo = null;
            if (categoryEntities != null) {
                catelog2Vo = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo1 = new Catelog2Vo(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> level3 = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3 != null) {
                        List<Catelog2Vo.Catalog3Vo> catelog3Vos = level3.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getParentCid().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo1.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo1;
                }).collect(Collectors.toList());
            }
            return catelog2Vo;
        }));
        return parent_cid;

    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {

        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid().equals(parent_cid);
        }).collect(Collectors.toList());
        return collect;
    }

    //递归向上查找父节点
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
        return children;
    }
}