import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.cloud.mall.thirdParty.component.SmsComponent;
import com.cloud.mall.thirdParty.thirdPartyMain;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = thirdPartyMain.class)
public class test{

    @Resource
    OSSClient ossClient;
    @Autowired
    SmsComponent smsComponent;


    @Test
    public void test06(){
        smsComponent.sendCode();
    }

    @Test
    public void testUpload() throws FileNotFoundException {

        //上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\k\\Desktop\\鱼类清单1的10种鱼照片\\鱼类清单1的10种鱼照片\\阿纳鲳鲹.jpg");
        ossClient.putObject("mall-ossdiv", "1.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功.");
    }

}
