package com.chilema.service;

import com.chilema.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface CategoryService extends IService<Category> {
    void removeById(Long id);
}
