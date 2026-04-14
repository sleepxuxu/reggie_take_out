package com.rui.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rui.reggie.dto.SetmealDto;
import com.rui.reggie.entity.Setmeal;
import com.rui.reggie.entity.SetmealDish;
import com.rui.reggie.mapper.SetmealMapper;
import com.rui.reggie.service.SetmealDishService;
import com.rui.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void saveWithSetmealDto(SetmealDto setmealDto) {

        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.forEach(item -> item.setSetmealId(setmealDto.getId()));

        setmealDishService.saveBatch(setmealDishes);
    }
}
