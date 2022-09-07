package com.chilema.dto;

import com.chilema.entity.Dish;
import com.chilema.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@ApiModel(value = "Dish数据传输对象")
@Data
public class DishDTO extends Dish {
    private List<DishFlavor> flavors=new ArrayList<>();
    private String categoryName;
}
