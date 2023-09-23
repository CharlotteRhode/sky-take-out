package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;




@Mapper
public interface SetmealDishMapper {


    //判断勾选的菜品是否已经关联了套餐
    List<Long> countRelatedDishByIds(List<Long> ids);




}
