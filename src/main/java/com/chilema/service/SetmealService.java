package com.chilema.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.dto.SetmealDTO;
import com.chilema.entity.Dish;
import com.chilema.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface SetmealService extends IService<Setmeal> {
    void addWithDish(SetmealDTO setmealDTO);
    void deleteByIds(Long[] ids);

    SetmealDTO getWithDishes(Long id);

    void updateWithDishes(SetmealDTO setmealDTO);

    List<SetmealDTO> list(SetmealDTO setmealDTO);
    Page queryPage(int page, int pageSize, String name);
    List<Dish> getDishesById( Long setmealId);
}
