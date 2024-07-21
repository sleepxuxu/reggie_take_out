package com.rui.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果， 服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class Result<T> {
    private Integer code; // 0成功， 1或其他为失败

    private String message;

    private T data;

    private Map map = new HashMap(); // 动态数据

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.code = 0;
        result.data = object;
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<T>();
        result.code = 1;
        result.message = message;
        return result;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key,value);
        return this;
    }
}
