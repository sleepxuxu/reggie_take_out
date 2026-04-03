package com.rui.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作 自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("公共字段--insert");
        log.debug(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long currentId = BaseContext.getThreadLocalId();
        if (currentId == null) {
            log.error("当前线程未设置用户ID，无法自动填充创建用户和更新用户");
            throw new CustomException("用户未登录，无法执行此操作");
        }
        metaObject.setValue("createUser", currentId);
        metaObject.setValue("updateUser", currentId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("公共字段--update");
        log.debug(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long currentId = BaseContext.getThreadLocalId();
        if (currentId == null) {
            log.error("当前线程未设置用户ID，无法自动填充更新用户");
            throw new CustomException("用户未登录，无法执行此操作");
        }
        metaObject.setValue("updateUser", currentId);
    }
}
