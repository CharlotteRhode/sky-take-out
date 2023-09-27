package com.sky.controller.user;


import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.server.WebSocketServer;
import com.sky.service.OrderDetailService;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private WebSocketServer webSocketServer;


    //用户下单
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) throws IOException {
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    //用户催单
    @GetMapping("/reminder/{id}")
    public Result orderRemind(@PathVariable OrdersSubmitDTO orderId) throws IOException {

        OrderSubmitVO orderVO = orderService.submit(orderId);


        //用web socket给admin推送催单消息：
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("type", 2);
        msgMap.put("orderId", orderId);
        msgMap.put("content", "order number is: " + orderVO.getOrderNumber());

        log.info("向管理端推送催单消息,{}", msgMap);

        webSocketServer.sendMsg(JSONObject.toJSONString(msgMap));

        return Result.success();
    }





}
