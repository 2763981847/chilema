package com.chilema.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    Page queryPage(int page, int pageSize, String number, String beginTime, String endTime,Long userId);

    Page queryPage(int page, int pageSize);
}
