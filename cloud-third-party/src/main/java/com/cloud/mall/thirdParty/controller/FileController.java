package com.cloud.mall.thirdParty.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);//PublicIndexController 该controller控制器的名字

    @PostMapping("/file")
    public String uploadMusicFile(HttpServletRequest request, @RequestParam("file") MultipartFile[] files){
        LOG.info("进入上传...");
        String uploadPath="D:/tempFile/";//存放到本地路径（示例）
        if(files!=null && files.length>=1) {
            BufferedOutputStream bw = null;
            try {
                String fileName = files[0].getOriginalFilename();
                //判断是否有文件
                if(StringUtils.isNoneBlank(fileName)) {
                    //输出到本地路径
                    File outFile = new File(uploadPath + UUID.randomUUID().toString()+getFileType(fileName));
                    LOG.info("path=="+uploadPath + UUID.randomUUID().toString()+ getFileType(fileName));
                    FileUtils.copyInputStreamToFile(files[0].getInputStream(), outFile);                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if(bw!=null) {bw.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }

    @RequestMapping(value = "/test",method = {RequestMethod.POST,RequestMethod.GET})
    public ModelAndView uploadImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("进入get方法！");

        MultipartHttpServletRequest req =(MultipartHttpServletRequest)request;
        MultipartFile multipartFile =  req.getFile("file");

        String realPath = "D:\\bfish";
        try {
            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file  =  new File(realPath, UUID.randomUUID()+".jpg");
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileType(String filename){
        if(filename.endsWith(".jpg") || filename.endsWith(".jepg")){
            return ".jpg";
        }else if(filename.endsWith(".png") || filename.endsWith(".PNG")){
            return ".png";
        } else{
            return "application/octet-stream";
        }
    }

}
