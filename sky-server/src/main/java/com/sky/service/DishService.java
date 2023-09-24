package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    //新增菜品：
    void save(DishDTO dishDTO);


    //根据条件分页查询菜品信息：
    PageResult page(DishPageQueryDTO queryDTO);


    //批量删除菜品：
    void delete(List<Long> ids);


    //修改菜品信息 - 1. 根据id查询并回显：
    DishVO getInfo(Long id);


    //修改菜品信息 - 2.修改（保存）：
    void update(DishDTO dishDTO);


    /**
     * 条件查询菜品和口味
     * @param queryDTO
     * @return
     */
    List<DishVO> listWithFlavor(DishPageQueryDTO queryDTO);



}

