package com.chilema.dto;

import com.chilema.entity.Dish;
import com.chilema.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@ApiModel(value = "Dish数据传输对象")
@Data
public class DishDTO extends Dish {
    @ApiModelProperty("菜品关联的口味列表")
    private List<DishFlavor> flavors=new ArrayList<>();
    @ApiModelProperty("菜品所属的分类名")
    private String categoryName;
}
