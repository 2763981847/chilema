package com.chilema.service;

import com.chilema.common.Result;
import com.chilema.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface UserService extends IService<User> {
    void sendMsg(User user);
    User login(HttpSession session,Map<String, String> map);

}
