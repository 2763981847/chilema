package com.chilema.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("基于ThreadLocal的工具类，用于存储当前用户的id")
public class BaseContext {
    @ApiModelProperty("该线程的用户id")
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void set(Long id) {
        threadLocal.set(id);
    }

    public static Long get() {
        return threadLocal.get();
    }
}
