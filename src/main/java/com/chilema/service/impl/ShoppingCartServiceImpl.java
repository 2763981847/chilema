package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chilema.common.BaseContext;
import com.chilema.entity.ShoppingCart;
import com.chilema.mapper.ShoppingCartMapper;
import com.chilema.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 拿到用户的购物车信息
     * @return 购物车列表
     */
    @Override
    public List<ShoppingCart> list() {
        //从线程中拿到用户id
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = super.list(wrapper);
        return list;
    }

    /**
     * 添加进购物车功能
     * @param shoppingCart 要添加的购物车信息
     */
    @Override
    public void add(ShoppingCart shoppingCart) {
        //从线程中拿到用户ID
        Long userId = BaseContext.get();
        //拿到用户的购物车数据
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        ShoppingCart one = super.getOne(wrapper);
        //用户新增菜品是之前没有的，创建一个新项
        if (one == null) {
            shoppingCart.setCreateTime(LocalDateTime.now());
            super.save(shoppingCart);
        }
        //用户新增菜品是已有的，在原来的数量上加一
        else {
            one.setNumber(one.getNumber() + 1);
            super.updateById(one);
        }
    }

    /**
     * 从购物车移除功能
     * @param shoppingCart 要移除的购物车
     */
    @Override
    public void sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(ShoppingCart::getUserId, BaseContext.get());
        ShoppingCart one = super.getOne(wrapper);
        //如果购物车中只有一件该菜品则直接去除掉这一项
        if (one.getNumber() == 1) {
            super.remove(wrapper);
        }
        //如果购物车中有大于一件该菜品，则数量减一
        else {
            one.setNumber(one.getNumber() - 1);
            super.updateById(one);
        }
    }

}
