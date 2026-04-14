package com.rui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rui.reggie.common.Result;
import com.rui.reggie.dto.SetmealDto;
import com.rui.reggie.entity.Setmeal;
import com.rui.reggie.service.SetmealDishService;
import com.rui.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealDishController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;


    @PostMapping("/save")
    public Result<String> save(@RequestBody SetmealDto setmealDto)  {
        log.info("save setmealDto:{}", setmealDto);

        setmealService.saveWithSetmealDto(setmealDto);
        return  Result.success("操作成功");
    }

    @GetMapping("/page")
    public Result<List<Setmeal>> page(Integer pageNum, Integer pageSize) {
        Page<Setmeal> page = new Page<>(pageNum, pageSize);

        // todo
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        setmealService.page(page, queryWrapper);

        List<Setmeal> setmealList = page.getRecords();

        return Result.success(setmealList);
    }
}
