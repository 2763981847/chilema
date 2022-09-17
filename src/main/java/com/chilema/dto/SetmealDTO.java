package com.chilema.dto;

import com.chilema.entity.Setmeal;
import com.chilema.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("套餐数据传输对象")
@Data
public class SetmealDTO extends Setmeal {
    @ApiModelProperty("套餐关联的菜品列表")
    private List<SetmealDish> setmealDishes;
    @ApiModelProperty("套餐所属的分类名")
    private String categoryName;
}
