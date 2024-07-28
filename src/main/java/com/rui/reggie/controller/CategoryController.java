package com.rui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rui.reggie.common.Result;
import com.rui.reggie.entity.Category;
import com.rui.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Category category) {
        log.info("category:{}",category);
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(Integer pageNum, Integer pageSize) {
        Page<Category> pageInfo = new Page<>(pageNum, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, lambdaQueryWrapper);
        return  Result.success(pageInfo);
    }

    /**
     * 根据id来删除分类
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public Result<String> delete(Long id) {
        log.info("删除分类id:{}",id);
        categoryService.removeById(id);

        return Result.success("删除成功");
    }
}
