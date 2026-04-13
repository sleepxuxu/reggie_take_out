package com.rui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rui.reggie.dto.DishDto;
import com.rui.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品、同时插入菜品对应的口味数据
     * @param dishDto
     */
    public void saveWithDishFlavor(DishDto dishDto);

    /**
     * 查询菜品详情
     */
    public DishDto getDishDtoById(Long dishId);


    /**
     * 修改菜品
     */
    public void updateDish(DishDto dishDto);
}
