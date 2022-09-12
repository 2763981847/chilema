package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import com.chilema.entity.ShoppingCart;
import com.chilema.service.ShoppingCartService;
import io.swagger.annotations.Api;
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
@Api("购物车类Controller")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @ApiOperation("查询用户购物车数据功能")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @ApiOperation("添加进购物车功能")
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.add(shoppingCart);
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
        shoppingCartService.sub(shoppingCart);
        return Result.success("移除成功");
    }
}
