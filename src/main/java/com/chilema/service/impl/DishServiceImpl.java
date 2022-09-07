package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.dto.DishDTO;
import com.chilema.entity.Dish;
import com.chilema.entity.DishFlavor;
import com.chilema.mapper.DishMapper;
import com.chilema.service.DishFlavorService;
import com.chilema.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Service

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品并将其口味存在口味表中
     *
     * @param dishDTO
     */
    @Transactional        //涉及到多张表的操作，需开启事务控制
    @Override
    public void addDishWithFlavors(DishDTO dishDTO) {
        super.save(dishDTO);
        for (DishFlavor flavor : dishDTO.getFlavors()) {
            flavor.setDishId(dishDTO.getId());
            dishFlavorService.save(flavor);
        }
    }

    /**
     * 根据id拿到对应菜品的信息和其口味
     *
     * @param id
     * @return
     */
    @Override
    public DishDTO getDishWithFlavorsById(Long id) {
        //根据菜品id拿到dish
        Dish dish = super.getById(id);
        DishDTO dishDTO = new DishDTO();
        //将已有的属性从dish拷贝到dishDTO
        BeanUtils.copyProperties(dish, dishDTO);
        //到菜品口味表中查询到该菜品的口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dish.getId());
        //将查询到的口味数据赋值到dishDTO中
        dishDTO.setFlavors(dishFlavorService.list(wrapper));
        return dishDTO;
    }

    /**
     * 更新菜品信息和口味
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateDishAndFlavors(DishDTO dishDTO) {
        //更新菜品信息
        super.updateById(dishDTO);
        //先把菜品关联的原口味删除
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDTO.getId());
        dishFlavorService.remove(wrapper);
        //再为菜品关联新口味
        for (DishFlavor flavor : dishDTO.getFlavors()) {
            flavor.setDishId(dishDTO.getId());
            dishFlavorService.save(flavor);
        }
    }

    /**
     * 删除菜品及其口味
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteDishAndFlavors(Long[] ids) {
        //先将菜品进行逻辑删除
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getIsDeleted, 1).in(Dish::getId, ids);
        super.update(updateWrapper);
        //再将菜品关联的口味进行逻辑删除
        LambdaUpdateWrapper<DishFlavor> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(DishFlavor::getIsDeleted, 1).in(DishFlavor::getDishId, ids);
        dishFlavorService.update(wrapper);
    }

    /**
     * 根据菜品分类拿到对应菜品的信息和其口味
     *
     * @param dishDTO
     * @return
     */
    @Override
    public List<DishDTO> list(DishDTO dishDTO) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dishDTO.getCategoryId() != null, Dish::getCategoryId, dishDTO.getCategoryId())
                .eq(Dish::getStatus, 1)
                .eq(Dish::getIsDeleted, 0)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = super.list(wrapper);
        List<DishDTO> dtoList = new ArrayList<>();
        for (Dish dish : list) {
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dish.getId())
                    .eq(DishFlavor::getIsDeleted, 0)
                    .orderByDesc(DishFlavor::getUpdateTime);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
            DishDTO dto = new DishDTO();
            BeanUtils.copyProperties(dish, dto);
            dto.setFlavors(dishFlavors);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
