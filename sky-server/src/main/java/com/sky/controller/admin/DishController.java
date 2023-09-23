package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.PeriodUnit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;


    //新增菜品：
    @PostMapping
    public Result createNewDish(@RequestBody DishDTO dishDTO){
        log.info("新增一个菜品, {}", dishDTO);

        dishService.save(dishDTO);

        return Result.success();
    }

    //根据条件分页查询菜品信息：
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO queryDTO){
        log.info("条件分页查询，{}", queryDTO);
        PageResult pageResult = dishService.page(queryDTO);
        return Result.success(pageResult);
    }

    //批量删除菜品：
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){//集合的注解
        log.info("delete dishes,{}", ids);
        dishService.delete(ids);
        return Result.success();

    }

    //修改菜品信息 - 1. 根据id查询并回显：
    @GetMapping("/{id}")
    public Result<DishVO> updateShowInfo(@PathVariable Long id){
        log.info("i want to update, show me info,{}", id);
       DishVO dishVO =  dishService.getInfo(id);
       return Result.success(dishVO);
    }

    //修改菜品信息 - 2.修改（保存）：
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }







}
