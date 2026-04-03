package com.rui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rui.reggie.common.Result;
import com.rui.reggie.dto.DishDto;
import com.rui.reggie.entity.Category;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.service.CategoryService;
import com.rui.reggie.service.DishFlavorService;
import com.rui.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/save")
    public Result<String> addDish(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithDishFlavor(dishDto);
        return Result.success("操作成功");
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> getDish(Integer page, Integer pageSize, String name) {

        Page<Dish> pageDish = new Page<>(page, pageSize);

        Page<DishDto> pageDishDto = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Dish::getName, name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageDish, queryWrapper);

        BeanUtils.copyProperties(pageDish, pageDishDto, "records");

        List<Dish> records = pageDish.getRecords();

        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = dishDto.getCategoryId();

            Category category = categoryService.getById(categoryId);

            dishDto.setCategoryName(category.getName());

            return dishDto;
        }).collect(Collectors.toList());

        pageDishDto.setRecords(list);

        return Result.success(pageDishDto);
    }


    /**
     * 菜品详情
     */

    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id) {

        DishDto dishDto = dishService.getDishDtoById(id);
        return Result.success(dishDto);
    }
}
