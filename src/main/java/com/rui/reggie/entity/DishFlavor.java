package com.rui.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品口味
 */
@Data
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long dishId;

    private String name;

    private String value;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    // 是否删除
    private Integer isDeleted;
}
