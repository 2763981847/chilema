package com.chilema.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 套餐菜品关系
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Getter
@Setter
@TableName("setmeal_dish")
@ApiModel(value = "SetmealDish对象", description = "套餐菜品关系")
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("套餐id ")
    @TableField("setmeal_id")
    private String setmealId;

    @ApiModelProperty("菜品id")
    @TableField("dish_id")
    private String dishId;

    @ApiModelProperty("菜品名称 （冗余字段）")
    @TableField("name")
    private String name;

    @ApiModelProperty("菜品原价（冗余字段）")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty("份数")
    @TableField("copies")
    private Integer copies;

    @ApiModelProperty("排序")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private Long createUser;

    @ApiModelProperty("修改人")
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @ApiModelProperty("是否删除")
    @TableField("is_deleted")
    private Integer isDeleted;
}
