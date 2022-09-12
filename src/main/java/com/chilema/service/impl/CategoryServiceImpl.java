package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chilema.common.MyException;
import com.chilema.entity.Category;
import com.chilema.entity.Dish;
import com.chilema.entity.Setmeal;
import com.chilema.mapper.CategoryMapper;
import com.chilema.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chilema.service.DishService;
import com.chilema.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类功能
     *
     * @param id 要删除的分类id
     */
    @Override
    public void removeById(Long id) {
        //判断该分类下是否关联有菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        if (dishService.count(dishLambdaQueryWrapper) > 0) {
            throw new MyException("该分类下关联有菜品，不能直接删除");
        }
        //判断该分类下是否关联有套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(setmealLambdaQueryWrapper) > 0) {
            throw new MyException("该分类下关联有套餐，不能直接删除");
        }
        //该分类下未关联任何菜品和套餐，可直接删除
        super.removeById(id);
    }
}
