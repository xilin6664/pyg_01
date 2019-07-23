package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDfsClient;


/**
 * 文件上传
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${STORAGE_SERVER}")
    private String STORAGE_SERVER;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            //1.连接到服务器
            FastDfsClient client = new FastDfsClient("classpath:config/fsdf_conf.conf");
            //2.上传文件
            String originalFilename = file.getOriginalFilename();//sdsad.sadgsa.jpg 原始文件名
            //从原始文件获取后缀 不要.
            String sufixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //3.接收返回的文件地址值
            String filePath = client.uploadFile(file.getBytes(), sufixName);//group2/M00/22/44/sgsgdsgasga.jpg
            //4.如果成功返回.返回地址:http://storageServer的IP/filepath
            return new Result(true, STORAGE_SERVER + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
