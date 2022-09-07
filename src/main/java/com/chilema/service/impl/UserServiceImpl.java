package com.chilema.service.impl;

import com.chilema.entity.User;
import com.chilema.mapper.UserMapper;
import com.chilema.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
