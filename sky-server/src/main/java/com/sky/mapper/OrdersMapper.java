package com.sky.mapper;


import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper {

    //新增订单 (需要返回主键id）
    void insert(Orders orders);
}
