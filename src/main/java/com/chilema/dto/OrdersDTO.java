package com.chilema.dto;

import com.chilema.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("订单数据传输对象")
@Data
public class OrdersDTO extends Orders {
    @ApiModelProperty("订单的总金额")
    private int sumNum;
}
