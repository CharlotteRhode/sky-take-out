package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    //添加到购物车
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
            shoppingCartService.add(shoppingCartDTO);
            return Result.success();
    }

    //查看购物车
    @GetMapping("/list")
    public Result<List<ShoppingCart>> showTheCart(){
        List<ShoppingCart> myCart = shoppingCartService.showAll();
        return Result.success(myCart);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public Result cleanCart(){
        shoppingCartService.cleanCart();
        return Result.success();
    }


}
