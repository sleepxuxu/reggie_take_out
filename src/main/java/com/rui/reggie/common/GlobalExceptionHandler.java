package com.rui.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param e
     * @return
     */

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        if(e.getMessage().contains("Duplicate entry")) {
           String[] messageList =  e.getMessage().split(" ");
           String message = messageList[2] + "已存在";
           return Result.error(message);
        }
        return Result.error("未知错误");
    }

    /**
     * 异常处理方法
     * @param e
     * @return
     */

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public Result<String> handleIOException(IOException e) {
        log.error("IO异常", e);
        return Result.error("文件操作失败，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统错误，请联系管理员");
    }
}
