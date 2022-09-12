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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("新增菜品功能")
    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishdto) {
        log.info("开始存入{}及其口味", dishdto.getName());
        dishService.addDishWithFlavors(dishdto);
        return Result.success("保存成功");
    }

    @ApiOperation("分页查询功能")
    @GetMapping("/page")
    public Result<Page> queryPage(int page, int pageSize, String name) {
        Page queryPage = dishService.queryPage(page, pageSize, name);
        return Result.success(queryPage);
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
        //更新了菜品信息，需要清理缓存
        Set<String> keys = new HashSet<>();
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            keys.add("dish_" + dish.getCategoryId() + "_" + dish.getStatus());
        }
        redisTemplate.delete(keys);
        //批量起售（停售）菜品
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
