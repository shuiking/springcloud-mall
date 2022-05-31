package com.cloud.mall.cloudproduct;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.cloud.mall.cloudproduct.dao.AttrGroupDao;
import com.cloud.mall.cloudproduct.dao.SkuSaleAttrValueDao;
import com.cloud.mall.cloudproduct.entity.BrandEntity;
import com.cloud.mall.cloudproduct.service.BrandService;
import com.cloud.mall.cloudproduct.service.CategoryService;
import com.cloud.mall.cloudproduct.service.SkuSaleAttrValueService;
import com.cloud.mall.cloudproduct.vo.Catelog2Vo;
import com.cloud.mall.cloudproduct.vo.SkuItemSaleAttrVo;
import com.cloud.mall.cloudproduct.vo.SpuItemAttrGroupVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
//@RunWith(SpringRunner.class)
@SpringBootTest
public class uploadTest {
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BrandService brandService;
    @Resource
    private OSSClient ossClient;
    @Resource
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    public void testDao01(){
//        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(4L);
//        for(SkuItemSaleAttrVo vo:saleAttrsBySpuId)
//        {
//            System.out.println("hhhh");
//            System.out.println(vo.getAttrValues());
//        }
    }

    @Test
    public void test06()
    {
//        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(4L, 225L);
//        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void testRedissonClient(){
//        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedisTemplate()
    {
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        ops.set("hello","world"+ UUID.randomUUID().toString());
//        String hello = ops.get("hello");
//        System.out.println(hello);
    }

    @Test
    public void test03(){
//        Long[] catelogPath = categoryService.findCatelogPath(225L);
//        log.info("完整路径{}", Arrays.asList(catelogPath));
    }
    @Test
    public void test02(){
// 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
//        String objectName = "hello1.jpg";
//        String bucketName = "mall-ossdiv";
//        String content = "C:\\Users\\k\\Pictures\\Saved Pictures\\DM_20220118233831_001.jpg";
//        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

// 关闭OSSClient。
//        ossClient.shutdown();
    }
    @Test
    public void test01(){
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tSYvsxMd9ieeH8gyk28";
//        String accessKeySecret = "4RvNayru92zzliIN1AQ11iC5jXyyJk";
// 填写Bucket名称，例如examplebucket。
//        String bucketName = "mall-ossdiv";
// 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
//        String objectName = "hello.jpg";

// 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        String content = "C:\\Users\\k\\Pictures\\Saved Pictures\\DM_20220118233831_001.jpg";
//        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

// 关闭OSSClient。
//        ossClient.shutdown();
    }
    @Test
    public void test04(){
//        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

    }

}
