package com.chilema.service;

import com.chilema.entity.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 地址管理 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface AddressBookService extends IService<AddressBook> {
    void add (AddressBook addressBook);
    void delete(Long ids);
    AddressBook getDefault();

}
