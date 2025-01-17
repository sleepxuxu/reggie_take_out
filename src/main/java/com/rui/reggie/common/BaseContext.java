package com.rui.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setThreadLocalId(Long id) {
        threadLocal.set(id);
    }

    public static Long getThreadLocalId() {
        return threadLocal.get();
    }
}
