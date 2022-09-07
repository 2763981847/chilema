package com.chilema.dto;

import com.chilema.entity.Setmeal;
import com.chilema.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("套餐数据传输对象")
@Data
public class SetmealDTO extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
