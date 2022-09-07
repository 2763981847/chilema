package com.chilema.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 地址管理
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Getter
@Setter
@TableName("address_book")
@ApiModel(value = "AddressBook对象", description = "地址管理")
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("收货人")
    @TableField("consignee")
    private String consignee;

    @ApiModelProperty("性别 0 女 1 男")
    @TableField("sex")
    private Byte sex;

    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("省级区划编号")
    @TableField("province_code")
    private String provinceCode;

    @ApiModelProperty("省级名称")
    @TableField("province_name")
    private String provinceName;

    @ApiModelProperty("市级区划编号")
    @TableField("city_code")
    private String cityCode;

    @ApiModelProperty("市级名称")
    @TableField("city_name")
    private String cityName;

    @ApiModelProperty("区级区划编号")
    @TableField("district_code")
    private String districtCode;

    @ApiModelProperty("区级名称")
    @TableField("district_name")
    private String districtName;

    @ApiModelProperty("详细地址")
    @TableField("detail")
    private String detail;

    @ApiModelProperty("标签")
    @TableField("label")
    private String label;

    @ApiModelProperty("默认 0 否 1是")
    @TableField("is_default")
    private Boolean isDefault;

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
