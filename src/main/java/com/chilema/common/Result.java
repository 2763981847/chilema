package com.chilema.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@ApiModel("返回的结果类")
@Data
public class Result<T> {
    @ApiModelProperty("状态码：1成功，0和其它数字为失败")
    private Integer code;
    @ApiModelProperty("返回的信息")
    private String msg;
    @ApiModelProperty("返回的数据")
    private T data;
    @ApiModelProperty("返回的动态数据")
    private Map map = new HashMap();

    @ApiOperation("返回成功")
    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    @ApiOperation("返回错误")
    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    @ApiOperation("向返回的结果中添加动态数据")
    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
