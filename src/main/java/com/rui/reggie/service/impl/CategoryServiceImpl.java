package com.rui.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rui.reggie.common.CustomException;
import com.rui.reggie.entity.Category;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.entity.Setmeal;
import com.rui.reggie.mapper.CategoryMapper;
import com.rui.reggie.service.CategoryService;
import com.rui.reggie.service.DishService;
import com.rui.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前做判断
     * @param id
     */
    @Override
    public void removeByCategoryId(Long id) {

        // 查询当前分类是否关联菜品，如果已关联， 抛出业务异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();

        dishQueryWrapper.eq(Dish::getCategoryId,id);

        int countA = dishService.count(dishQueryWrapper);

        if(countA > 0) {
            // 已关联，抛出异常
            throw new CustomException("当前分类下关联菜品, 不能删除");
        }

        // 查询当前分类是否关联套餐，如果已关联， 抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int countB = setmealService.count(setmealQueryWrapper);

        if(countB > 0) {
            throw new CustomException("当前分类下关联套餐, 不能删除");
        }

        // 正常删除
        super.removeById(id);
    }
}
