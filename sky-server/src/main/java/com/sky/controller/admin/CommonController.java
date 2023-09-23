package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;


    //图片上传
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("uploading image...{}", file);


        //获取原文件的后缀
        String originalFilename = file.getOriginalFilename(); // 1.2.3.jpg
        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));

        //创建要往阿里云上传的新的文件名
        String objName =  UUID.randomUUID().toString() + extName;

        //调用工具类 - 上传文件：
        String url = aliOssUtil.upload(file.getBytes(), objName);

        return Result.success(url);
    }


}
