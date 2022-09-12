package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import com.chilema.entity.AddressBook;
import com.chilema.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Api("用户地址簿类")
@Slf4j
@RestController
@RequestMapping("addressBook")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;

    @ApiOperation("用户新增地址功能")
    @PostMapping
    public Result<String> add(@RequestBody AddressBook addressBook) {
        addressBookService.add(addressBook);
        return Result.success("新增地址成功");
    }

    @ApiOperation("地址更新功能")
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success("保存成功");
    }

    @ApiOperation("地址删除功能")
    @DeleteMapping
    public Result<String> delete(@RequestParam Long ids) {
        addressBookService.delete(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("用户地址展示功能")
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        Long userId = BaseContext.get();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDeleted, 0);
        List<AddressBook> list = addressBookService.list(wrapper);
        return Result.success(list);
    }

    @ApiOperation("设置为默认地址功能")
    @PutMapping("/default")
    public Result<String> updateDefault(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.get();
        //先将原有的默认地址更改为非默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getIsDefault, false)
                .eq(AddressBook::getUserId, userId);
        addressBookService.update(updateWrapper);
        //在修改要修改为默认地址的地址
        addressBook.setIsDefault(true);
        addressBookService.updateById(addressBook);
        return Result.success("设置成功");
    }

    @ApiOperation("获取默认地址功能")
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = addressBookService.getDefault();
        //没有拿到默认地址，返回错误信息
        if (addressBook == null) return Result.error("您尚未设置默认地址");
        return Result.success(addressBook);
    }

    @ApiOperation("地址信息回显功能")
    @GetMapping("/{addressId}")
    public Result<AddressBook> dataEcho(@PathVariable Long addressId) {
        AddressBook addressBook = addressBookService.getById(addressId);
        return Result.success(addressBook);
    }
}
