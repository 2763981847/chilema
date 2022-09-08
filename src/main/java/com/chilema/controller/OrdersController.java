package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import com.chilema.dto.OrdersDTO;
import com.chilema.entity.OrderDetail;
import com.chilema.entity.Orders;
import com.chilema.service.OrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Api("订单类Controller")
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @ApiOperation("订单提交功能")
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return Result.success("订单提交成功");
    }

    @ApiOperation("（用户端）订单分页展示功能")
    @GetMapping("/userPage")
    public Result<Page> queryPage(@RequestParam int page, @RequestParam int pageSize) {
        Page queryPage = ordersService.queryPage(page, pageSize);
        return Result.success(queryPage);
    }

    @ApiOperation("(管理端)订单分页展示功能")
    @GetMapping("/page")
    public Result<Page> page(@RequestParam int page, @RequestParam int pageSize, String number, String beginTime, String endTime) {
        Page queryPage = ordersService.queryPage(page, pageSize, number, beginTime, endTime, null);
        return Result.success(queryPage);
    }

    @ApiOperation("订单派送功能")
    @PutMapping
    public Result<String> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success("派送成功");
    }

}
