package com.chilema.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.entity.Employee;
import com.chilema.service.EmployeeService;
import com.chilema.service.impl.EmployeeServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
@Slf4j
@RestController
@RequestMapping("/employee")
@Api("员工类Controller")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @ApiOperation("登录功能")
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //根据用户名查找该用户
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("username", employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        //没有找到该用户，返回
        if (emp == null) {
            return Result.error("找不到该用户");
        }
        //确定该用户是否处于被禁用状态，被禁用，返回
        if (emp.getStatus() == 0) {
            return Result.error("该账户已被禁用");
        }
        // 找到该用户，比对密码是否正确,密码错误，返回
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        //登录成功，将用户id放到session中
        request.getSession().setAttribute("employee", emp.getId());
        log.info("当前线程id为{}", Thread.currentThread().getId());
        return Result.success(emp);
    }

    @ApiOperation("登出功能")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    @ApiOperation("添加员工功能")
    @PostMapping
    public Result<String> addEmployee(HttpSession session, @RequestBody Employee employee) {
        log.info("新增员工的员工信息" + employee.toString());
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        employee.setPassword(password);
        employeeService.save(employee);
        log.info("用户{}保存成功", employee.getUsername());
        return Result.success("保存成功");
    }

    @ApiOperation("分页查询功能")
    @GetMapping("/page")
    public Result<Page> queryPage(int page, int pageSize, String name) {
        log.info("接收到参数：page:{},pageSize:{},name:{}", page, pageSize, name);
        Page pageInfo = new Page(page, pageSize);          //Page对象的两个参数--当前页数和页面容量
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        wrapper.orderByDesc("update_time");
        employeeService.page(pageInfo, wrapper);          //employeeService.page()方法会将pageInfo更新，所以没必要新建对象，直接返回pageInfo即可
        return Result.success(pageInfo);
    }

    @ApiOperation("员工禁用(启动)功能")
    @PutMapping
    public Result<String> updateEmployee(HttpSession session, @RequestBody Employee employee) {
        log.info("要修改的员工id是{}", employee.getId());
        employeeService.updateById(employee);
        return Result.success("员工已信息修改成功");
    }

    @ApiOperation("数据回显功能")
    @GetMapping("/{id}")
    public Result<Employee> dataEcho(@PathVariable Long id) {
        log.info("数据回显···");
        return Result.success(employeeService.getById(id));
    }
}
