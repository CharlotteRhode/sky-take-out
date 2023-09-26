package com.sky.mapper;


import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {


    //保存详细菜单
    void insertBatch(List<OrderDetail> orderDetailList);
}
