package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DishFlavorMapper {


    //保存菜品口味信息到dish_flavor表：
    void insertBatch(List<DishFlavor> flavors);


    //删除口味信息
    void deleteFlavor(List<Long> ids);


    //修改菜品 - 2. 拿到口味信息
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getFlavorInfo(Long id);
}
