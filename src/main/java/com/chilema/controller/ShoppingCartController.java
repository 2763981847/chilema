package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import com.chilema.entity.ShoppingCart;
import com.chilema.service.ShoppingCartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @ApiOperation("查询用户购物车数据功能")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return Result.success(list);
    }

    @ApiOperation("添加进购物车功能")
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.get();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one == null) {
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        } else {
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }
        return Result.success("添加成功");
    }

    @ApiOperation("清空购物车功能")
    @DeleteMapping("/clean")
    public Result<String> clean() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.get());
        shoppingCartService.remove(wrapper);
        return Result.success("清除成功");
    }

    @ApiOperation("从购物车中移除功能")
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(ShoppingCart::getUserId, BaseContext.get());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one.getNumber() == 1) {
            shoppingCartService.remove(wrapper);
        } else {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return Result.success("移除成功");
    }
}
