package com.chilema.controller;

import com.chilema.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

/**
 * <p>
 * 文件上传（下载） 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@RestController
@RequestMapping("/common")
@Api("文件类Controller")
public class FileController {
    @ApiModelProperty("配置的文件转存路径")
    @Value("${chilema.path}")
    String basePath;

    @ApiOperation("文件上传功能")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info(file.toString());
        //获取文件的格式
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //使用UUID防止上传的文件名重复
        String fileName = UUID.randomUUID() + suffix;
        //防止basePath目录不存在，提前创建
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //把文件名返回到前端，方便前端使用该图片
        return Result.success(fileName);
    }

    @ApiOperation("文件下载功能")
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //通过文件输入流读取到文件
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            //通过输出流将文件写到浏览器上
            ServletOutputStream outputStream = response.getOutputStream();
            //设置相应文件的格式
            response.setContentType("image/jpeg");
            //开始输出文件
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                //刷新数据
                outputStream.flush();
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
