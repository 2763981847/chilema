package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.MyException;
import com.chilema.common.Result;
import com.chilema.entity.User;
import com.chilema.service.UserService;
import com.chilema.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Api("用户类Controller")
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("获取随机验证码功能")
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(HttpSession session, @RequestBody User user) {
        String code = userService.sendMsg(user);
        return Result.success(code);
    }

    @ApiOperation("用户登录功能")
    @PostMapping("/login")
    public Result<User> login(HttpSession session, @RequestBody Map<String, String> map) {
        User user = userService.login(session, map);
        if (user == null) return Result.error("验证码错误");
        return Result.success(user);
    }

    @ApiOperation("用户登出功能")
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        //将session中的用户信息清除再登出
        session.invalidate();
        return Result.success("登出成功");
    }

}
