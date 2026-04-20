package com.rui.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rui.reggie.dto.SetmealDto;
import com.rui.reggie.entity.Setmeal;
import com.rui.reggie.entity.SetmealDish;
import com.rui.reggie.mapper.SetmealMapper;
import com.rui.reggie.service.SetmealDishService;
import com.rui.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    @Override
    public SetmealDto getSetmealDtoById(Long setmealId) {
        Setmeal setmeal = baseMapper.selectById(setmealId);

        if (setmeal == null) {
            return null;
        }

        // 查询套餐关联的菜品列表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        queryWrapper.orderByAsc(SetmealDish::getSort);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        // 组装SetmealDto
        SetmealDto setmealDto = new SetmealDto();
        // 复制Setmeal属性到SetmealDto
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Transactional
    @Override
    public void updateWithSetmealDto(SetmealDto setmealDto) {
        // 更新套餐基本信息
        this.updateById(setmealDto);

        // 删除原有菜品关联
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        // 保存新菜品关联
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item -> item.setSetmealId(setmealDto.getId()));
        setmealDishService.saveBatch(setmealDishes);
    }
}
