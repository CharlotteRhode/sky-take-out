package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j

public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */

    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登陆: {}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);


        //生成GWT令牌：
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        //把employee封装到EmployeeLoginVO当中
        EmployeeLoginVO employeeLoginVO = new EmployeeLoginVO(employee.getId(),employee.getUsername(), employee.getName(), jwt);


        return Result.success(employeeLoginVO);
    }


    //新增员工：
    @PostMapping
    public Result saveNew(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工,{}", employeeDTO);

        employeeService.save(employeeDTO);

        return Result.success();
    }


    //员工的分页查询：
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO pageQueryDTO){
            PageResult pageResult = employeeService.page(pageQueryDTO);
            return Result.success(pageResult);
    }



    //启用/禁用员工：
    @ApiOperation("启用/禁用员工")
    @PostMapping ("/status/{status}")
    public Result enableOrDisable(@PathVariable Integer status, Long id){
        employeeService.enable(status,id);
        return Result.success();
    }



    //编辑员工 - 第一部：根据员工的id查询出员工信息，并回显：
    @ApiOperation("编辑员工-根据id查询信息并且回显")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
            Employee employee = employeeService.getById(id);
            return Result.success(employee);
    }

    //编辑员工 - 第二步： 编辑，当点击（保存）按钮时：
    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result edit(@RequestBody EmployeeDTO employeeDTO){
        employeeService.edit(employeeDTO);
        return Result.success();
    }






    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
