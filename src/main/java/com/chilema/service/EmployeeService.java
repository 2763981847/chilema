package com.chilema.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chilema.common.Result;
import com.chilema.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 员工信息 服务类
 * </p>
 *
 * @author 付秋杰
 * @since 2022-08-28
 */
public interface EmployeeService extends IService<Employee> {
   void   addEmployee( Employee employee);
   Page queryPage(int page, int pageSize, String name);
}
