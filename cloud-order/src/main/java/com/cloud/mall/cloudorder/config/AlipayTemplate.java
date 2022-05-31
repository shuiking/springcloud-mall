package com.cloud.mall.cloudorder.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.cloud.mall.cloudorder.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000120616499";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCvlngFlKVJWeGHHfEWP07t/w5OXUwTg1LY6VG/cUo/gs7srNbbi4VCThwQkL5/3J5QtEccnzWsX3qLFRQO69mhoYrOwdV+3wT5xkwWGjeb7XstShqED4/vEOiWbdjgNdm/fl4rHpNTTYYokrBF7EuEMUbaxwNviW5rzLsIxwKlQi8EUMG2KbK8cGSImvkLkSID5lTx7gv8Ie3YbugZyo+Q85Aw0Vu90Axl/GDZ7HJY7T0S0/BhgVYM1oBmnZVQeA96GNk4JATtEmscWjlv3OvC60D0oUrVkKy3dqL7hjLVRlgUndi5NLxKJxLlkfDqUpXbGXyu9W69TTaXpKLRVGeDAgMBAAECggEAYxtcDVJUZO2NhVVaIcOECoN/EK5KZW0nUvWGxbbtQKHVp/L6pxq+EanC33rDe6oL1eObi8U1Z2DQ6zXvh2iKA1gJ4B+6II1C4rSRd5w+rQ1DDFIaW1XpWPfDQD0qP4tFp3dtMYtlfoxwX/BD/IOeMDQcGRphjxAUOlZpb0/c52dc0/3wS8ozuaF+0jcdsQsfdAc1ZAGGkyAtK6uum5GjdWd83tteUmA9JYlk4xzlADR2P0PRQtGVqjUqdUN3+AFBZDRyrPcal49S9t7SRdGuL+ugIYr/ptwKVwHIyiEGzr+uTYP++O9ItskI0N1MOnpL/vA3yxCqGbGwNUYZ14ni+QKBgQD2aHkfmJeIw/HFyK7CMUuLuoDaVUBHVd6HK7C3/VbftPcQ/XqaMkqOKO+Tp6q0XcLbfNB/jMaP2a8b+etMCnb+/rq9ogOrRE5a8XGxqf5rb+6IVsupOdQv757sYCpmiLckl5uwdXYQnd031MDnfIVi1BXFZe5iWVki1HkHkWlo/wKBgQC2bEA+O3p1Ev+92JFB51phSaXbwUoSp2u2JuuVzPzjQFz0Tv+24dMMdx+7vvfO+/4pjwXveT2Dz9nwcHKp3GwBdgTjX9tFl4GoBmkZrraP+ziuT4NdX0ev4c0LC7V7iYUDfhmNbjNv+3nMKM86238Th7Vj88wR48yWw0IXNsjdfQKBgQDRhEHBYPEwZFP2tc4K5vlyhRG9FBIE1fnR+49W0r4El44gzzdpV2vXdu1HIqDenn7qPDXYzq2jcRgY9Kdiz98gsR290r3F6d2qLHRc+cuQNOug2BRx/u7yF5CNMrO63XLEVXkXULkkJecM6wVfj5ynTOh6IVLSI2DMYdfLXJRBpwKBgFbj2PXLftik5AuZTBcIC5Srb8TsnEumUze/8ha4PPxBfAUiW8rQZaZaV1Ml8wM0JCvHZBxcs2BFcJmF9hfSVd+cR1fGILtVHvzZkNYa0fpivuzIEto2t/6envvT5+2f4yt2tNUY2IdOUQVdqtduFWlICq5BQcKDrr9g4eoMvkBBAoGAZEm/hK2GG2Dmi6/E5zhAOfXr8OADG3N0NBKyw0hbDsehqc+Tt//99V2lGA3M1jAJV6VCgcCYWufAKnSjwHyGugrg+oP1cOVyv6zGnI59JS4euDHYjjNQ0vI4QHFwv0NlHsAvd8ROKYr8blcipnk1xsiuQMNXEtRnICA3CC67DWk=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmI+ONqBXxcs3Ui8mrVi9wf4G4/z3LlF6d00UHu/4R/F+GouIIUOxi0SCYr8+qcWvOpiD8ex3/rFlPJVuSESbHXp2BIXV4/HVFHNwO+IZ8uqljZJe6FunEjoZDAfVHVna08EQwpA+eybnYnyt4B8bsXCD284wIamOyxOCNpUPzIY+qXhQbZn0aCnpWnYcRSZ0v9oR2tYaYlXGG5v8dP8rrLi1w0bZI7W3DpCJThhHUXa8dtCbvLy3qhtQKbNfH8lXz5A2yxMkXxz9smaoiG9oHWj1opNTAzjM7PI8RTT7L2Fwpve4dn7n15oYRFbRGjVMh/Hj/MYohhEQSPZIR9cDqwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="https://61c446d550.zicp.fun/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://order.cloudmall.com:9002/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                +"\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
