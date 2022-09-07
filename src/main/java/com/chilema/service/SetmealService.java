package com.chilema.service;

import com.chilema.dto.SetmealDTO;
import com.chilema.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
