package com.rui.reggie.controller;

import com.rui.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String path;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String newFilename = UUID.randomUUID().toString() + suffix;
        try {
            file.transferTo(new File(path + newFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Result.success(newFilename);
    }


    @GetMapping("/download")
    public void download(String filename, HttpServletResponse response) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(path + filename));
             ServletOutputStream outputStream = response.getOutputStream();) {
            response.setContentType("application/pdf");
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
