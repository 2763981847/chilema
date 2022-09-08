package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.entity.Category;
import com.chilema.service.CategoryService;
import com.chilema.service.impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Api("菜品（套餐）分类Controller")
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @ApiOperation("新增分类")
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        log.info("新增分类信息{}", category.toString());
        return Result.success("保存成功");
    }

    @ApiOperation("分页查询功能")
    @GetMapping("/page")
    public Result<Page> queryPage(int page, int pageSize) {
        log.info("接收到参数：page:{},pageSize:{}", page, pageSize);
        Page categoryPage = new Page(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage, wrapper);
        return Result.success(categoryPage);
    }

    @ApiOperation("套餐信息更新功能")
    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        log.info("更新的分类信息{}", category.toString());
        return Result.success("保存成功");
    }

    @ApiOperation("套餐删除功能")
    @DeleteMapping
    public Result<String> deleteCategory(Long id) {
        categoryService.removeById(id);
        log.info("id为{}的套餐已被删除", id);
        return Result.success("删除成功");
    }

    @ApiOperation("菜品分类查询功能")
    @GetMapping("/list")
    public Result<List<Category>> queryCategoryByType(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return Result.success(list);
    }
}
