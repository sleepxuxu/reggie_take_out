package com.rui.reggie.controller;

import com.rui.reggie.common.CustomException;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    @Value("${reggie.upload.allowed-extensions}")
    private String allowedExtensions;

    @Value("${reggie.upload.max-size}")
    private long maxSize;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file.isEmpty()) {
            throw new CustomException("上传文件不能为空");
        }

        // 2. 检查文件大小
        if (file.getSize() > maxSize) {
            throw new CustomException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomException("文件名不能为空");
        }

        // 3. 检查文件名安全（防止路径遍历攻击）
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new CustomException("文件名包含非法字符");
        }

        // 4. 提取文件扩展名并校验
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        // 解析允许的扩展名
        Set<String> allowedExtSet = new HashSet<>(Arrays.asList(allowedExtensions.split(",")));

        if (!allowedExtSet.contains(suffix.toLowerCase())) {
            throw new CustomException("不支持的文件类型，仅支持: " + allowedExtensions);
        }

        // 5. 生成安全的新文件名
        String newFilename = UUID.randomUUID().toString() + suffix;

        // 6. 确保上传目录存在
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 7. 保存文件
        File destFile = new File(path + newFilename);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new CustomException("文件上传失败: " + e.getMessage());
        }

        log.info("文件上传成功: {}", newFilename);
        return Result.success(newFilename);
    }


    @GetMapping("/download")
    public void download(String filename, HttpServletResponse response) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new CustomException("文件名不能为空");
        }

        // 防止路径遍历攻击
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new CustomException("文件名包含非法字符");
        }

        File file = new File(path + filename);
        if (!file.exists()) {
            throw new CustomException("文件不存在: " + filename);
        }

        // 根据文件扩展名设置Content-Type
        String contentType = getContentType(filename);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        try (FileInputStream fileInputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("文件下载失败: {}", filename, e);
            throw new CustomException("文件下载失败");
        }
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }
}
