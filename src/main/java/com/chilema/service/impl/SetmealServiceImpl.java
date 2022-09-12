package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.MyException;
import com.chilema.dto.SetmealDTO;
import com.chilema.entity.Dish;
import com.chilema.entity.Setmeal;
import com.chilema.entity.SetmealDish;
import com.chilema.mapper.SetmealMapper;
import com.chilema.service.CategoryService;
import com.chilema.service.DishService;
import com.chilema.service.SetmealDishService;
import com.chilema.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealDishService setmealDishService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增套餐及其关联菜品信息
     *
     * @param setmealDTO 套餐数据传输对象
     */
    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public void addWithDish(SetmealDTO setmealDTO) {
        //先将套餐信息保存
        super.save(setmealDTO);
        //再将套餐关联的菜品存入关联表中
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(String.valueOf(setmealDTO.getId())));
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐及其菜品关联信息
     *
     * @param ids 要删除的套餐的id数组
     */
    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", allEntries = true)
    public void deleteByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new MyException("请先选择删除对象");
        }
        //先将套餐进行逻辑删除
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Setmeal::getId, ids)
                .set(Setmeal::getIsDeleted, 1)
                .set(Setmeal::getStatus, 0);
        super.update(wrapper);
        //再将套餐与菜品的关联信息进行删除
        LambdaUpdateWrapper<SetmealDish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SetmealDish::getSetmealId, ids)
                .set(SetmealDish::getIsDeleted, 1);
        setmealDishService.update(updateWrapper);
    }

    /**
     * 数据回显
     *
     * @param id 套餐id
     * @return 套餐数据传输对象
     */
    @Override
    public SetmealDTO getWithDishes(Long id) {
        //先拿到套餐基础信息
        Setmeal setmeal = super.getById(id);
        SetmealDTO setmealDTO = new SetmealDTO();
        //将已有的套餐基础信息拷贝给套餐数据传输对象
        BeanUtils.copyProperties(setmeal, setmealDTO);
        //拿到套餐关联的所有菜品并传给套餐数据传输对象
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id).eq(SetmealDish::getIsDeleted, 0);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDTO.setSetmealDishes(list);
        return setmealDTO;
    }

    /**
     * 更新套餐信息和其菜品关联信息
     *
     * @param setmealDTO 套餐数据传输对象
     */
    @Override
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public void updateWithDishes(SetmealDTO setmealDTO) {
        //先将套餐信息进行更新
        super.updateById(setmealDTO);
        //先在关联表中删除所有修改前的套餐关联的菜品的关联信息
        LambdaUpdateWrapper<SetmealDish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealDTO.getId())
                .set(SetmealDish::getIsDeleted, 1);
        setmealDishService.update(wrapper);
        //再像关联表中存入新的关联信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(String.valueOf(setmealDTO.getId())));
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 展示套餐列表
     *
     * @param setmealDTO 套餐数据传输对象
     * @return 查询到的所有套餐列表
     */
    @Override
    @Cacheable(value = "setmealCache", key = "#setmealDTO.categoryId")
    public List<SetmealDTO> list(SetmealDTO setmealDTO) {
        //先拿到套餐的基本信息
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmealDTO.getCategoryId() != null, Setmeal::getCategoryId, setmealDTO.getCategoryId())
                .eq(Setmeal::getStatus, 1)
                .eq(Setmeal::getIsDeleted, 0)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = super.list(wrapper);
        //在拿到套餐所对应的菜品关联信息
        List<SetmealDTO> dtoList = new ArrayList<>();
        for (Setmeal setmeal : list) {
            SetmealDTO dto = new SetmealDTO();
            BeanUtils.copyProperties(setmeal, dto);
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, dto.getId())
                    .eq(SetmealDish::getIsDeleted, 0)
                    .orderByAsc(SetmealDish::getSort)
                    .orderByDesc(SetmealDish::getUpdateTime);
            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
            dto.setSetmealDishes(dishes);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 根据条件分页查询
     *
     * @param page     页码
     * @param pageSize 单页数据条数
     * @param name     套餐名
     * @return 查询到的分页对象
     */
    @Override
    public Page queryPage(int page, int pageSize, String name) {
        log.info("接收到分页查询数据，页数：{}，单页大小：{}，查询菜品名：{}", page, pageSize, name);
        //构建分页对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDTO> dtoPage = new Page<>();
        //进行分页查询
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name)
                .eq(Setmeal::getIsDeleted, 0)
                .orderByDesc(Setmeal::getUpdateTime);
        super.page(setmealPage, wrapper);
        //将setmealPage的属性值拷贝给dtoPage
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        //拿setmealList中的每一个的分类id到其对应的分类名并赋值给dtoList;
        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDTO> dtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            Long categoryId = setmeal.getCategoryId();
            SetmealDTO setmealDTO = new SetmealDTO();
            BeanUtils.copyProperties(setmeal, setmealDTO);
            setmealDTO.setCategoryName(categoryService.getById(categoryId).getName());
            dtoList.add(setmealDTO);
        }
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /**
     * 根据套餐id拿到其关联的菜品
     * @param setmealId 套餐id
     * @return 菜品列表
     */
    @Override
    public List<Dish> getDishesById(Long setmealId) {
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        List<Dish> dishes = new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Dish dish = dishService.getById(setmealDish.getDishId());
            dishes.add(dish);
        }
        return dishes;
    }
}
