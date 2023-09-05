package com.sky.mapper;

import com.sky.Annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     */

    @Select("select * from employee where username = #{username}")
    public Employee getByUsername(String username);



    //新增员工：
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);



    //员工分页查询:(动态查询）
    List<Employee> list(String name);

    //启用/禁用员工：
    //或（公用的接口）
    //编辑/更新员工信息：
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);


    //编辑员工 - 第一部：根据员工的id查询出员工信息，并回显：
    @Select("select * from Employee where id = #{id}")
    Employee getbyId(Long id);




}
