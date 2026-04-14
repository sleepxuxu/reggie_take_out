package com.rui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rui.reggie.dto.SetmealDto;
import com.rui.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithSetmealDto(SetmealDto setmealDto);
}
