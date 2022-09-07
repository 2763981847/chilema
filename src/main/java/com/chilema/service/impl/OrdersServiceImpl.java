package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.BaseContext;
import com.chilema.common.MyException;
import com.chilema.common.Result;
import com.chilema.dto.OrdersDTO;
import com.chilema.entity.*;
import com.chilema.mapper.OrdersMapper;
import com.chilema.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Resource
    UserService userService;
    @Resource
    AddressBookService addressBookService;
    @Resource
    ShoppingCartService shoppingCartService;
    @Resource
    OrderDetailService orderDetailService;

    /**
     * 提交订单功能
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //拿到用户id;
        Long userId = BaseContext.get();
        //拿到用户购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();
        shoppingCartWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new MyException("当前购物车为空，不能进行下单");
        }
        //拿到用户数据
        User user = userService.getById(userId);
        //拿到用户地址簿信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new MyException("不存在该地址");
        }
        //随机生成一个订单号
        long orderId = IdWorker.getId();
        //计算订单总金额，并且生成订单详情列表
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //填充订单的其余信息
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(addressBook.getConsignee());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(shoppingCartWrapper);
    }

    /**
     * 分页展示订单功能
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page queryPage(int page, int pageSize, String number, String beginTime, String endTime, Long userId) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Orders::getUserId, userId)
                .eq(number != null, Orders::getNumber, number)
                .gt(beginTime != null, Orders::getOrderTime, beginTime)
                .lt(endTime != null, Orders::getOrderTime, endTime)
                .orderByDesc(Orders::getOrderTime);
        super.page(ordersPage, wrapper);
        Page<OrdersDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, dtoPage, "records");
        List<Orders> orders = ordersPage.getRecords();
        List<OrdersDTO> dtos = new ArrayList<>();
        for (Orders order : orders) {
            OrdersDTO dto = new OrdersDTO();
            BeanUtils.copyProperties(order, dto);
            Long orderId = order.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, orderId);
            int sum = 0;
            for (OrderDetail orderDetail : orderDetailService.list(queryWrapper)) {
                sum += orderDetail.getNumber();
            }
            dto.setSumNum(sum);
            dtos.add(dto);
        }
        dtoPage.setRecords(dtos);
        return dtoPage;
    }

    @Override
    public Page queryPage(int page, int pageSize) {
        return queryPage(page, pageSize, null, null, null, BaseContext.get());
    }

}
