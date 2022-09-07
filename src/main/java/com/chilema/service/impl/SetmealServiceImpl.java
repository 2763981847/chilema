package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.MyException;
import com.chilema.dto.SetmealDTO;
import com.chilema.entity.Setmeal;
import com.chilema.entity.SetmealDish;
import com.chilema.mapper.SetmealMapper;
import com.chilema.service.SetmealDishService;
import com.chilema.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealDishService setmealDishService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增套餐及其关联菜品信息
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void addWithDish(SetmealDTO setmealDTO) {
        super.save(setmealDTO);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(String.valueOf(setmealDTO.getId())));
        setmealDishService.saveBatch(setmealDishes);
        //新增了套餐，需要清理缓存
        redisTemplate.delete("setmeal_" + setmealDTO.getCategoryId() + "_" + setmealDTO.getStatus());
    }

    /**
     * 删除套餐及其菜品关联信息
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new MyException("请先选择删除对象");
        }
        //删除套餐前，需要清理缓存
        Set<String> keys = new HashSet<>();
        for (Long id : ids) {
            Setmeal setmeal = super.getById(id);
            keys.add("setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus());
        }
        redisTemplate.delete(keys);
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
     * @param id
     * @return
     */
    @Override
    public SetmealDTO getWithDishes(Long id) {
        Setmeal setmeal = super.getById(id);
        SetmealDTO setmealDTO = new SetmealDTO();
        BeanUtils.copyProperties(setmeal, setmealDTO);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id).eq(SetmealDish::getIsDeleted, 0);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDTO.setSetmealDishes(list);
        return setmealDTO;
    }

    /**
     * 更新套餐信息和其菜品关联信息
     *
     * @param setmealDTO
     */
    @Override
    public void updateWithDishes(SetmealDTO setmealDTO) {
        super.updateById(setmealDTO);
        LambdaUpdateWrapper<SetmealDish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealDTO.getId())
                .set(SetmealDish::getIsDeleted, 1);
        setmealDishService.update(wrapper);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(String.valueOf(setmealDTO.getId())));
        setmealDishService.saveBatch(setmealDishes);
        //更新了菜品，需要清理缓存
        redisTemplate.delete("setmeal_" + setmealDTO.getCategoryId() + "_" + setmealDTO.getStatus());
    }

    /**
     * 展示套餐列表
     * @param setmealDTO
     * @return
     */
    @Override
    public List<SetmealDTO> list(SetmealDTO setmealDTO) {
        Long categoryId = setmealDTO.getCategoryId();

        //先从Redis中尝试拿到数据
        String key = "setmeal_" + categoryId + "_" + setmealDTO.getStatus();
        List<SetmealDTO> dtoList = (List<SetmealDTO>) (redisTemplate.opsForValue().get(key));
        if (dtoList != null) {
            //redis中有数据，直接返回
            return dtoList;
        }
        //未从Redis中拿到数据，查询数据库
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmealDTO.getCategoryId() != null, Setmeal::getCategoryId, setmealDTO.getCategoryId())
                .eq(Setmeal::getStatus, 1)
                .eq(Setmeal::getIsDeleted, 0)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = super.list(wrapper);
        dtoList = new ArrayList<>();
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
        //拿到数据，存到Redis中
        redisTemplate.opsForValue().set(key, dtoList, 60, TimeUnit.MINUTES);
        return dtoList;
    }
}
