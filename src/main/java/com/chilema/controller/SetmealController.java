package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.dto.DishDTO;
import com.chilema.dto.SetmealDTO;
import com.chilema.entity.Dish;
import com.chilema.entity.Setmeal;
import com.chilema.entity.SetmealDish;
import com.chilema.service.CategoryService;
import com.chilema.service.DishService;
import com.chilema.service.SetmealDishService;
import com.chilema.service.SetmealService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Resource
    private SetmealService setmealService;
    @Resource
    private SetmealDishService setmealDishService;
    @Resource
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private DishService dishService;

    @ApiOperation("添加套餐功能")
    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("套餐信息为{}", setmealDTO);
        setmealService.addWithDish(setmealDTO);
        return Result.success("新增成功");
    }

    @ApiOperation("分页查询功能")
    @GetMapping("/page")
    public Result<Page> queryPage(int page, int pageSize, String name) {
        log.info("接收到分页查询数据，页数：{}，单页大小：{}，查询菜品名：{}", page, pageSize, name);
        //构建分页对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDTO> dtoPage = new Page<>();
        //进行分页查询
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name)
                .eq(Setmeal::getIsDeleted, 0)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, wrapper);
        //将setmealPage的属性值拷贝给dtoPage
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        //拿setmealList中的每一个的分类id到其对应的分类名并赋值给dtoList;
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDTO> dtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            Long categoryId = setmeal.getCategoryId();
            SetmealDTO setmealDTO = new SetmealDTO();
            BeanUtils.copyProperties(setmeal, setmealDTO);
            setmealDTO.setCategoryName(categoryService.getById(categoryId).getName());
            dtoList.add(setmealDTO);
        }
        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }

    @ApiOperation("(批量)删除套餐功能")
    @DeleteMapping
    public Result<String> deleteSetmeal(@RequestParam Long[] ids) {
        setmealService.deleteByIds(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("(批量)起售（停售）功能")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable int status, @RequestParam Long[] ids) {
        //更新了套餐，需要清理缓存
        Set<String> keys = new HashSet<>();
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            keys.add("setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus());
        }
        redisTemplate.delete(keys);
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Setmeal::getId, ids)
                .set(Setmeal::getStatus, status);
        setmealService.update(wrapper);
        return Result.success("修改成功");
    }

    @ApiOperation("数据回显功能")
    @GetMapping("/{id}")
    public Result<SetmealDTO> dataEcho(@PathVariable Long id) {
        SetmealDTO setmealDTO = setmealService.getWithDishes(id);
        return Result.success(setmealDTO);
    }

    @ApiOperation("套餐信息修改功能")
    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateWithDishes(setmealDTO);
        return Result.success("修改成功");
    }

    @ApiOperation("根据套餐相关信息查询套餐功能")
    @GetMapping("/list")
    public Result<List<SetmealDTO>> list(SetmealDTO setmealDTO) {
        List<SetmealDTO> list = setmealService.list(setmealDTO);
        return Result.success(list);
    }

    @ApiOperation("根据套餐id返回其包含的菜品功能")
    @GetMapping("/dish/{setmealId}")
    public Result<List<Dish>> getDishesById(@PathVariable Long setmealId) {
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        List<Dish> dishes = new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Dish dish = dishService.getById(setmealDish.getDishId());
            dishes.add(dish);
        }
        return Result.success(dishes);
    }
}
