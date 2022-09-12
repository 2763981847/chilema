package com.chilema.service;

import com.chilema.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    List<ShoppingCart> list();
    void add( ShoppingCart shoppingCart);
    void sub( ShoppingCart shoppingCart);

}
