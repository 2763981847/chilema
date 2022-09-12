package com.chilema.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.entity.Employee;
import com.chilema.mapper.EmployeeMapper;
import com.chilema.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    /**
     * 新增员工功能
     *
     * @param employee 要新增的员工信息
     */
    @Override
    public void addEmployee(Employee employee) {
        log.info("新增员工的员工信息" + employee.toString());
        //设置默认密码
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        employee.setPassword(password);
        //保存员工
        super.save(employee);
        log.info("用户{}保存成功", employee.getUsername());
    }

    /**
     *  根据条件查询分页功能
     * @param page 页码
     * @param pageSize 单页数据条数
     * @param name 员工名
     * @return 查询到的分页对象
     */
    @Override
    public Page queryPage(int page, int pageSize, String name) {
        log.info("接收到参数：page:{},pageSize:{},name:{}", page, pageSize, name);
        Page pageInfo = new Page(page, pageSize);          //Page对象的两个参数--当前页数和页面容量
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        //若有根据姓名查询的条件就添加条件
        if (StringUtils.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        wrapper.orderByDesc("update_time");
        super.page(pageInfo, wrapper);          //employeeService.page()方法会将pageInfo更新，所以没必要新建对象，直接返回pageInfo即可
        return pageInfo;
    }
}
