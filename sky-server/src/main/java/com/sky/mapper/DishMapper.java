package com.sky.mapper;

import com.sky.Annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    //新增菜品：
    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id") //获取主键id
    @Insert("insert into Dish(name, category_id, price, image,description, status, create_time, update_time,create_user,update_user) values " +
            "(#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}) ")
    void insert(Dish dish);



    //根据条件分页查询菜品信息：
    List<DishVO> list(DishPageQueryDTO queryDTO);




    //统计起售菜品的数量：
    Long countEnableDishByIds(List<Long> ids);


    //删除基本信息
    void deleteBasic(List<Long> ids);

    //1. 修改菜品 - 1. 拿到基本信息
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);


    //修改菜品信息 - 2.修改（保存）：
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);






}
