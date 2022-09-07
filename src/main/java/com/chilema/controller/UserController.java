package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.BaseContext;
import com.chilema.common.MyException;
import com.chilema.common.Result;
import com.chilema.entity.User;
import com.chilema.service.UserService;
import com.chilema.utils.ValidateCodeUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("获取随机验证码功能")
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        log.info("接收到手机号{}", phone);
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode4String(6);
            log.info("随机验证码为{}", code);
            session.setAttribute(phone, code);
            return Result.success("验证码获取成功");
        }
        throw new MyException("手机号不能为空");
    }

    @ApiOperation("用户登录功能")
    @PostMapping("/login")
    public Result<User> login(HttpSession session, @RequestBody Map<String, String> map) {
        if (map.get("code") != null && map.get("code").equals(session.getAttribute(map.get("phone")))) {
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getPhone, map.get("phone"));
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setPhone(map.get("phone"));
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            session.removeAttribute(map.get("phone"));
            return Result.success(user);
        }
        return Result.error("验证码错误");
    }

    @ApiOperation("用户登出功能")
    @PostMapping("/logout")
    public  Result<String> logout(HttpSession session){
        session.invalidate();
        return Result.success("登出成功");
    }

}
