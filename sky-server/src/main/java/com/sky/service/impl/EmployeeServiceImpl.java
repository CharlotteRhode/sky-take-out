package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 员工登录
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {

        String incomingPassword = employeeLoginDTO.getPassword();

        //1.调用mapper，查询这个员工的信息；根据username;
        Employee DataBaseEmployee = employeeMapper.getByUsername(employeeLoginDTO.getUsername());

        //2. 判断这个员工是否存在，如果不存在，返回错误信息；
        if (DataBaseEmployee == null){
                log.info("查询到的员工信息为空，直接返回错误信息");
                throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //3.如果存在，校验密码，如果不正确，返回错误信息；
        //首先对页面传进来的明文密码进行md5加密处理：
        String md5Pass = DigestUtils.md5DigestAsHex(incomingPassword.getBytes());

        if (! md5Pass.equals(DataBaseEmployee.getPassword())){
            log.info("密码比对错误");
            throw new BaseException(MessageConstant.PASSWORD_ERROR);
        }


        //4. 判断status，如果禁用，返回错误信息；
        if (DataBaseEmployee.getStatus() == StatusConstant.DISABLE){
            log.info("此员工为禁用状态");
            throw new BaseException(MessageConstant.ACCOUNT_LOCKED);
        }

        return DataBaseEmployee;
    }



    //新增员工：
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //1.补全实体属性:DTO里有前段传递过来的属性值，entity里没有，所以现在要把dto里的属性copy到entity里：
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置密码（加密的）
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置状态：
        employee.setStatus(StatusConstant.ENABLE);

       /* //设置4个属性：
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateTime(LocalDateTime.now());

        //from interceptor - threadlocal：
        employee.setCreateUser(BaseContext.getCurrentId());//当前登录的人ID
        employee.setUpdateUser(BaseContext.getCurrentId());//当前登录的人ID*/

        //2. 调用mapper：
        employeeMapper.insert(employee);

    }


    //员工分页查询：
    @Override
    public PageResult page(EmployeePageQueryDTO pageQueryDTO) {

        //use PageHelper:
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        List<Employee> employeeList = employeeMapper.list(pageQueryDTO.getName());

        Page<Employee> page = (Page<Employee>)employeeList;

        return new PageResult(page.getTotal(), page.getResult());
    }




    //启用/禁用员工：
    @Override
    public void enable(Integer status, Long id) {
        Employee employee= Employee.builder()
                .id(id)
                .status(status)
                /*.updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())*/
                .build();

        employeeMapper.update(employee);
    }



    //编辑员工 - 第一部：根据员工的id查询出员工信息，并回显：
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getbyId(id);
        employee.setPassword("****");
        return  employee;
    }


    //编辑员工 - 第二步： 编辑，当点击（保存）按钮时：
    @Override
    public void edit(EmployeeDTO employeeDTO) {

        //把dto里传进来的属性 -> 复制给Employee
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employeeMapper.update(employee);
    }


}
