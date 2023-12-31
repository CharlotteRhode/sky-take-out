package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {


    //添加到购物车
    void add(ShoppingCartDTO shoppingCartDTO);


    //查看购物车
    List<ShoppingCart> showAll();


    //清空购物车
    void cleanCart();
}
