package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.dto.DishDTO;
import com.chilema.entity.Dish;
import com.chilema.service.CategoryService;
import com.chilema.service.DishFlavorService;
import com.chilema.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Api("菜品类Controller")
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @ApiOperation("新增菜品功能")
    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishdto) {
        log.info("开始存入{}及其口味", dishdto.getName());
        dishService.addDishWithFlavors(dishdto);
        return Result.success("保存成功");
    }

    /**
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation("分页查询功能")
    @GetMapping("/page")
    public Result<Page> queryPage(int page, int pageSize, String name) {
        log.info("接收到分页查询数据，页数：{}，单页大小：{}，查询菜品名：{}", page, pageSize, name);
        //构建分页对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDTO> dtoPage = new Page<>();
        //进行分页查询
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name)
                .eq(Dish::getIsDeleted, 0)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, wrapper);
        //将dishPage的属性值拷贝给dtoPage
        BeanUtils.copyProperties(dishPage, dtoPage, "records");
        //拿dishList中的每一个的分类id到其对应的分类名并赋值给dtoList;
        List<Dish> dishList = dishPage.getRecords();
        List<DishDTO> dtoList = new ArrayList<>();
        for (Dish dish : dishList) {
            Long categoryId = dish.getCategoryId();
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(dish, dishDTO);
            dishDTO.setCategoryName(categoryService.getById(categoryId).getName());
            dtoList.add(dishDTO);
        }
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }

    @ApiOperation("数据回显功能")
    @GetMapping("/{id}")
    public Result<DishDTO> dataEcho(@PathVariable Long id) {
        log.info("进行数据回显");
        return Result.success(dishService.getDishWithFlavorsById(id));
    }

    @ApiOperation("更新菜品信息功能")
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDTO dishdto) {
        log.info("开始更新{}信息及其口味", dishdto.getName());
        dishService.updateDishAndFlavors(dishdto);
        return Result.success("保存成功");
    }

    @ApiOperation("（批量）起售（停售）功能")
    @PostMapping("status/{status}")
    public Result<String> updateStatus(@PathVariable int status, @RequestParam Long[] ids) {
        log.info(status == 0 ? "停售商品" : "起售商品");
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status).in(Dish::getId, ids);
        dishService.update(updateWrapper);
        return Result.success("修改成功");
    }

    @ApiOperation("（批量）删除功能")
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam Long[] ids) {
        dishService.deleteDishAndFlavors(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("根据菜品相关信息查询菜品功能")
    @GetMapping("/list")
    public Result<List<DishDTO>> queryDishByCategoryId(DishDTO dishDTO) {
        List<DishDTO> list = dishService.list(dishDTO);
        return Result.success(list);
    }
}