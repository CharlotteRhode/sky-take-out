package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.yaml.snakeyaml.events.Event;


@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
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














    /*public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }*/

}
