package com.rui.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 更新菜品基本信息
     */
    void updateDish(Dish dish);

    /**
     * 根据菜品id删除菜品口味
     */
    void deleteDishFlavorByDishId(Long dishId);

    /**
     * 批量保存菜品口味
     */
    void insertDishFlavors(@Param("list") List<DishFlavor> dishFlavors);
}
