package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */

    Employee login(EmployeeLoginDTO employeeLoginDTO);


    //新增员工：
    void save(EmployeeDTO employeeDTO);


    //员工分页查询
    PageResult page(EmployeePageQueryDTO pageQueryDTO);

    //启用/禁用员工：
    void enable(Integer status, Long id);


    //编辑员工 - 第一部：根据员工的id查询出员工信息，并回显：
    Employee getById(Long id);

    //编辑员工 - 第二步： 编辑，当点击（保存）按钮时：
    void edit(EmployeeDTO employeeDTO);
}
