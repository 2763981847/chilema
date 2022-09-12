package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.MyException;
import com.chilema.common.Result;
import com.chilema.entity.User;
import com.chilema.mapper.UserMapper;
import com.chilema.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chilema.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 发送验证码功能
     *
     * @param user 要发送的用户对象
     */
    @Override
    public void sendMsg(User user) {
        String phone = user.getPhone();
        log.info("接收到手机号{}", phone);
        if (StringUtils.isNotEmpty(phone)) {
            //获取一个随机验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("随机验证码为{}", code);
            //将验证码存入Redis中并设置五分钟过期时间
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            //将验证码存入session中
            //session.setAttribute(phone, code);
            return;
        }
        //手机号为空，抛出异常
        throw new MyException("手机号不能为空");
    }

    /**
     * 用户登录功能
     *
     * @param session 会话对象
     * @param map     传入的参数
     * @return 登录用户的信息
     */
    @Override
    public User login(HttpSession session, Map<String, String> map) {
        //从session中拿到验证码
        //String code = (String) session.getAttribute(map.get("phone"));
        //从Redis中拿到验证码
        String code = (String) redisTemplate.opsForValue().get(map.get("phone"));
        if (map.get("code") != null && map.get("code").equals(code)) {
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getPhone, map.get("phone"));
            User user = super.getOne(wrapper);
            //若该手机号未登录过则创建一个新用户
            if (user == null) {
                user = new User();
                user.setPhone(map.get("phone"));
                super.save(user);
            }
            //在session中存入登录用户的id
            session.setAttribute("user", user.getId());
            //从session中移除验证码
            //session.removeAttribute(map.get("phone"));
            //从Redis中移除验证码
            redisTemplate.delete(map.get("phone"));
            return user;
        }
        return null;
    }
}
