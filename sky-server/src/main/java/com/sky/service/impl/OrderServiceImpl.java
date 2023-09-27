package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.BaseException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.server.WebSocketServer;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WebSocketServer webSocketServer;


    //用户下单
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) throws IOException {
        //查询收货地址：收货地址为空，不能下单：
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车为空，也不能下单：
        ShoppingCart currentCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> myCartContents = shoppingCartMapper.checkMyCart(currentCart);
        if(CollectionUtils.isEmpty(myCartContents)){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        //保存订单数据
        //dto -> copy to -> Orders
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        //补充前端没有传递过来的字段
        orders.setNumber(String.valueOf(System.nanoTime()));//生成一个订单号
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);

        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getDetail());

        //新增（保存）订单
        ordersMapper.insert(orders);




        //保存购物车明细数据(entity-Shopping_cart):
        List<OrderDetail> orderDetailList = myCartContents.stream().map(i -> {
            //拷贝属性
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(i, orderDetail);

            //赋值订单id
            orderDetail.setOrderId(orders.getId());
            return orderDetail;
        }).collect(Collectors.toList());

        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户的购物车数据//todo checkMyCart(currentCart)?
        shoppingCartMapper.checkMyCart(currentCart);

        //组装数据
        OrderSubmitVO result = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

        //用web socket给admin推送来单提醒：
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("type", 1);
        msgMap.put("orderId", orders.getId());
        msgMap.put("content", "order number is: " + orders.getNumber());

        log.info("向管理端推送来单提醒消息,{}", msgMap);

        webSocketServer.sendMsg(JSONObject.toJSONString(msgMap));

        return result;
    }





}
