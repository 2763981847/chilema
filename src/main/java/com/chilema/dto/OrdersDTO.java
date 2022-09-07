package com.chilema.dto;

import com.chilema.entity.Orders;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("订单数据传输对象")
@Data
public class OrdersDTO extends Orders {
    private int sumNum;
}
