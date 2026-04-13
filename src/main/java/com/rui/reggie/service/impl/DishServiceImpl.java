package com.rui.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rui.reggie.dto.DishDto;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.entity.DishFlavor;
import com.rui.reggie.mapper.DishMapper;
import com.rui.reggie.service.DishFlavorService;
import com.rui.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithDishFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> dishFlavors = dishDto.getDishFlavors();

        if (CollectionUtils.isNotEmpty(dishFlavors)) {
            dishFlavors.forEach(item -> item.setDishId(dishId));
            baseMapper.insertDishFlavors(dishFlavors);
        }
    }

    @Override
    public DishDto getDishDtoById(Long dishId) {

        Dish dish = getById(dishId);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);

        dishDto.setDishFlavors(dishFlavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateDish(DishDto dishDto) {
        // 更新菜品基本信息（使用 XML SQL）
        baseMapper.updateDish(dishDto);

        // 删除该菜品的旧口味信息
        baseMapper.deleteDishFlavorByDishId(dishDto.getId());

        // 保存新的口味信息
        List<DishFlavor> dishFlavors = dishDto.getDishFlavors();
        if (CollectionUtils.isNotEmpty(dishFlavors)) {
            dishFlavors.forEach(item -> item.setDishId(dishDto.getId()));
            baseMapper.insertDishFlavors(dishFlavors);
        }
    }


}
