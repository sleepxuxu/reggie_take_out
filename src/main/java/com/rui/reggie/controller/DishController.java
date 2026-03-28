package com.rui.reggie.controller;

import com.rui.reggie.common.Result;
import com.rui.reggie.dto.DishDto;
import com.rui.reggie.entity.Dish;
import com.rui.reggie.service.DishFlavorService;
import com.rui.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/save")
    public Result<String> addDish(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithDishFlavor(dishDto);
        return  Result.success("操作成功");
    }
}
