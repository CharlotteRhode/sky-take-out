package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.BaseException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Slf4j
@Service

public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    //新增菜品：
    @Transactional //这两个功能是一个
    @Override
    public void save(DishDTO dishDTO) {
        //1. 保存基本表dish ：
        //DishDTO -> copy to -> Dish:
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dish.setStatus(StatusConstant.DISABLE);

        dishMapper.insert(dish);

        //2. 保存口味表dish_flavor:
        List<DishFlavor> flavors = dishDTO.getFlavors();

        flavors.forEach(i -> {
            i.setDishId(dish.getId());
        });

        dishFlavorMapper.insertBatch(flavors);




    }

    //根据条件分页查询菜品信息：
    @Override
    public PageResult page(DishPageQueryDTO queryDTO) {
        //设置分页参数
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        //执行查询
        List<DishVO> dishVOList = dishMapper.list(queryDTO);
        //封装结果
        Page<DishVO> page = (Page<DishVO>) dishVOList;
        return new PageResult(page.getTotal(), page.getResult());
    }


    //批量删除菜品：
    @Transactional //一个方法里操作了多次数据库
    @Override
    public void delete(List<Long> ids) {
        //1. 判断菜品的起售/停售状态
        //select count(*) from dish where id in(1,2,3) and status=1;
        Long count = dishMapper.countEnableDishByIds(ids);
        if (count > 0) {//勾选的这批里有正在起售状态的菜
            throw new BaseException(MessageConstant.DISH_ON_SALE);
        }

        //2. 判断这个菜品是否关联了哪个套餐
        List<Long> countRelatedIds = setmealDishMapper.countRelatedDishByIds(ids);
        if (!CollectionUtils.isEmpty(countRelatedIds)) {
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //3. 删除菜品，并删除口味
        dishMapper.deleteBasic(ids);//删除基本信息
        dishFlavorMapper.deleteFlavor(ids);//删除口味信息



    }


    //修改菜品信息 - 1. 根据id查询并回显：
    @Override
    public DishVO getInfo(Long id) {
        //1. 拿到基本信息
        Dish dish = dishMapper.getById(id);
        //2. 拿到口味信息
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getFlavorInfo(id);
        //3. 组装信息
        DishVO dishVOCopy = new DishVO();
        BeanUtils.copyProperties(dish, dishVOCopy);

        if (dishVOCopy != null) {
            dishVOCopy.setFlavors(dishFlavorList);
        }

        return dishVOCopy;
    }


    //修改菜品信息 - 2.修改（保存）：
    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        //1. 根据id修改基本信息-dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.update(dish);


        //2. 根据菜品id修改口味信息-dish_flavor - 先删，后加：
        dishFlavorMapper.deleteFlavor(Collections.singletonList(dish.getId()));


        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(i -> {
            i.setDishId(dish.getId());
        });
        dishFlavorMapper.insertBatch(flavors);



    }


    /**
     * 条件查询菜品和口味
     *
     * @param dishPageQueryDTO
     * @return
     */
    public List<DishVO> listWithFlavor(DishPageQueryDTO dishPageQueryDTO) {
        //1.  查询缓存，
        String redisKey = "dish:cache" + dishPageQueryDTO;

        List<DishVO> redisResult = (List<DishVO>) redisTemplate.opsForValue().get(redisKey);
        //如果缓存中有此数据，直接返回结果
        if (!CollectionUtils.isEmpty(redisResult)) {
            log.info("redis has the data, return it from redis");
            return redisResult;
        }


        //2.如果缓存里没有此数据，查询数据库
        List<DishVO> dishList = dishMapper.list(dishPageQueryDTO);

        List<DishVO> dishVOList = new ArrayList<>();

        for (DishVO d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getFlavorInfo(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        //3. 把数据库中查询到的结果，加入缓存中
        redisTemplate.opsForValue().set(redisKey, dishVOList);
        log.info("redis is empty, go to SQL, insert data from SQL into redis");

        return dishVOList;
    }


    //起售/停售菜品
    @Override
    public void startOrStop(Long id, Integer status) {
        //1. 更新菜品状态信息
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
        //2. 如果是停售操作，还需要将菜品关联的套餐也停售了
        if (status == StatusConstant.DISABLE) {
            List<Long> setmealIds = setmealDishMapper.countRelatedDishByIds(Collections.singletonList(id));
            if (!CollectionUtils.isEmpty(setmealIds)) {
                setmealIds.stream().forEach(i -> {
                    Setmeal setmeal = Setmeal.builder().id(i).status(StatusConstant.DISABLE).build();
                    setmealMapper.updateSetmeal(i);
                });
            }
        }


    }






}




