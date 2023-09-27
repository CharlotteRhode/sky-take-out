package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    //关闭超时未支付的订单
    @Scheduled(cron = "0/30 * * * * ?") //每隔30秒钟执行一次
    public void cancelOrder(){
        //查询是否有符合条件的订单（待支付 + 15分钟之前下的单）
        LocalDateTime targetTime = LocalDateTime.now().minusMinutes(15);
        List<Orders> overtimeOrders =  ordersMapper.checkOvertimeOrders(Orders.PENDING_PAYMENT,targetTime );


        //如果有，取消订单(修改订单的状态）
        if (!CollectionUtils.isEmpty(overtimeOrders)){
            overtimeOrders.stream().forEach(eachOrder->{

                log.info("执行取消订单的定时任务，{}", eachOrder.getId());

                eachOrder.setStatus(Orders.CANCELLED);
                eachOrder.setCancelTime(LocalDateTime.now());
                eachOrder.setCancelReason("overtime payment");

                ordersMapper.update(eachOrder);
            });
        }
    }




    //每天凌晨1点，自动关闭未完成的订单
    @Scheduled(cron = "0 0 1 * * ?") //每隔30秒钟执行一次
    public void completeOrder() {
        //查询是否有符合条件的订单（派送中+2小时前下的单）
        LocalDateTime targetTime = LocalDateTime.now().minusHours(2);
        List<Orders> stillDeliveringOrders =  ordersMapper.checkOvertimeOrders(Orders.DELIVERY_IN_PROGRESS,targetTime );

        //如果有，修改订单状态
        if (!CollectionUtils.isEmpty(stillDeliveringOrders)){
            stillDeliveringOrders.stream().forEach(eachOrder->{
                eachOrder.setStatus(Orders.COMPLETED);
                eachOrder.setDeliveryTime(LocalDateTime.now());

                ordersMapper.update(eachOrder);
            });
        }

    }








}
