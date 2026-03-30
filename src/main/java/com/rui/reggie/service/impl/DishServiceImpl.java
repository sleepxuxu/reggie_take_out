package com.rui.reggie.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rui.reggie.dto.DishDto;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.entity.DishFlavor;
import com.rui.reggie.mapper.DishMapper;
import com.rui.reggie.service.DishFlavorService;
import com.rui.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
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
            dishFlavorService.saveBatch(dishFlavors);
        }
    }
}
