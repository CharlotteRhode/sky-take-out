package com.sky.mapper;


import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrderReportDTO;
import com.sky.dto.TurnOverReportDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrdersMapper {

    //新增订单 (需要返回主键id）
    void insert(Orders orders);

    //查询是否有符合条件的订单（待支付 + 15分钟之前下的单）
    @Select("select * from orders where status=#{status} and order_time < #{targetTime}")
    List<Orders> checkOvertimeOrders(Integer status, LocalDateTime targetTime);

    //更新订单状态 //todo: XML没写完整
    void update(Orders eachOrder);


    //获取指定日期范围内的营业额turnover list:
    List<TurnOverReportDTO> checkTurnOverAmount(LocalDateTime beginTime, LocalDateTime endTime);

    //查询指定日期范围内的订单数量：
    List<OrderReportDTO> countOrderByOrderTime(LocalDateTime beginTime, LocalDateTime endTime);

    //销量统计
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
