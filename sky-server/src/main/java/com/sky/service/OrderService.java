package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

import java.io.IOException;

public interface OrderService {



    //用户下单
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) throws IOException;
}
