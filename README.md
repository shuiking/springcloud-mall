## 项目介绍
本系统采用微服务架构设计,在分布式环境下利用Spring Cloud框架,通过业务划分,设计独立模块的微服务，拆分为订单服务、购物车服务、支付服务、用户管理服务、商品管理服务、文件上传服务等模块，结合了当前比较流行的互联网电商模式,为消费者提供商品贸易平台。

## 组织结构
springcloud-mall  
├── cloud-common -- 工具类及通用代码  
├── renren-generator -- 人人开源项目的代码生成器  
├── cloud-auth -- 认证中心（社交登录、OAuth2.0)  
├── cloud-cart -- 购物车服务  
├── cloud-coupon -- 优惠卷服务  
├── cloud-gateway -- 统一配置网关  
├── cloud-order -- 订单服务  
├── cloud-product -- 商品服务  
├── cloud-search -- 检索服务  
├── cloud-third-party -- 第三方服务（对象存储、短信)  
├── cloud-ware -- 仓储服务  
├── cloud-member -- 会员服务  
