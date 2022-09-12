package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import com.chilema.entity.AddressBook;
import com.chilema.mapper.AddressBookMapper;
import com.chilema.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 地址管理 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    /**
     * 新增地址功能
     *
     * @param addressBook 地址信息
     */
    @Override
    public void add(AddressBook addressBook) {
        //先新增地址
        addressBook.setUserId(BaseContext.get());
        log.info("新增的地址信息为：{}", addressBook);
        Long userId = addressBook.getUserId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDeleted, 0);
        //若用户没有其他地址，则将该地址设为默认地址
        if (super.count(wrapper) == 0) {
            addressBook.setIsDefault(true);
        }
        super.save(addressBook);
    }

    /**
     * 删除地址功能
     *
     * @param ids 要删除的地址id
     */
    @Override
    public void delete(Long ids) {
        //构建一个更新wrapper
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getId, ids)
                .set(AddressBook::getIsDeleted, 1)
                .set(AddressBook::getIsDefault, false);
        //若用户删除的是默认地址，则将剩下的一个地址设置为默认地址
        if (getDefault() == null) {
            LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AddressBook::getUserId, BaseContext.get())
                    .eq(AddressBook::getIsDeleted, 0);
            AddressBook addressBook = super.getOne(queryWrapper);
            addressBook.setIsDefault(true);
            super.updateById(addressBook);
        }
        super.update(wrapper);
    }

    /**
     * 获取默认地址功能
     *
     * @return 默认地址，若没有默认地址则返回空
     */
    @Override
    public AddressBook getDefault() {
        //从线程中拿到用户ID
        Long userId = BaseContext.get();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, true)
                .eq(AddressBook::getIsDeleted, 0);
        AddressBook addressBook = super.getOne(wrapper);
        return addressBook;
    }
}
