package com.chilema.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.dto.DishDTO;
import com.chilema.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface DishService extends IService<Dish> {
    void addDishWithFlavors(DishDTO dishDTO);

    DishDTO getDishWithFlavorsById(Long id);

    void updateDishAndFlavors(DishDTO dishDTO);

    void deleteDishAndFlavors(Long[] ids);

    List<DishDTO> list(DishDTO dishDTO);
    Page queryPage(int page, int pageSize, String name);
}
