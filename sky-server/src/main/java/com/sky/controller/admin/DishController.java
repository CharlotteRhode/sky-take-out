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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    //新增菜品：
    @PostMapping
    public Result createNewDish(@RequestBody DishDTO dishDTO){
        log.info("新增一个菜品, {}", dishDTO);

        dishService.save(dishDTO);


        //删除redis缓存中的数据：
        redisTemplate.delete("dish:cache" + dishDTO.getCategoryId());
        log.info("新增菜品： 缓存中之前保存的数据已清空");



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

        //删除redis缓存中的数据：直接全删
        Set keys = redisTemplate.keys("dish:cache:*");
        redisTemplate.delete(keys);
        log.info("删除菜品，删除缓存中所有dish开头的数据");


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


        //修改redis缓存中的数据：直接全删
        Set keys = redisTemplate.keys("dish:cache:*");
        redisTemplate.delete(keys);
        log.info("修改菜品，删除缓存中所有dish开头的数据");



        return Result.success();
    }



    //起售/停售菜品
    @PostMapping("/status/{status}/{id}")
    public Result startOrStop(@PathVariable Integer status, @PathVariable Long id){
        dishService.startOrStop(id, status);


        //停售/起售redis缓存中的数据：直接全删
        Set keys = redisTemplate.keys("dish:cache:*");
        redisTemplate.delete(keys);
        log.info("修改菜品售卖状态，删除缓存中所有dish开头的数据");


        return Result.success();
    }







}
