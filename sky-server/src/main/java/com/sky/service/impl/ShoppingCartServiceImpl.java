package com.sky.service.impl;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.interfaces.ECKey;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    //添加商品到购物车
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //1. 查询当前在购物车里，有没有此菜品（口味）/套餐？

        //获取当前用户的id:
        Long currentUserId = BaseContext.getCurrentId();
        //去数据库里查询dishId, flavor, setmealId：用entity->ShoppingCart：
        ShoppingCart myCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, myCart);

        myCart.setUserId(currentUserId);
        //shopping cart里有还是没有？
        List<ShoppingCart> showShoppingCart = shoppingCartMapper.checkMyCart(myCart);



        //2.如果已经存在，数量+1;
        if (!CollectionUtils.isEmpty(showShoppingCart)){
            ShoppingCart item = showShoppingCart.get(0);//这个商品
            item.setNumber(item.getNumber() + 1);
            shoppingCartMapper.updateItemNumber(item);
        }

        //3.如果不存在，添加一条新数据：
        //先要判断一下->添加进购物车的是dish,还是setmeal：
        Long currentDishId = myCart.getDishId();
        if (currentDishId != null){
            //添加的是单个菜品
            //组装购物车最后显示的数据：
            Dish currentDish = dishMapper.getById(currentDishId);
            myCart.setAmount(currentDish.getPrice());
            myCart.setImage(currentDish.getImage());
            myCart.setName(currentDish.getName());
        }else {
            //添加的是套餐
            //组装购物车最后显示的数据：
            List<DishItemVO> currentSetmeal = setmealMapper.getDishItemBySetmealId(myCart.getSetmealId());
            //todo:缺一个方法

        }

        myCart.setCreateTime(LocalDateTime.now());
        myCart.setNumber(1);

        shoppingCartMapper.insert(myCart);
    }


    //查看购物车
    @Override
    public List<ShoppingCart> showAll() {
        //获取当前用户的id：
        ShoppingCart currentUserCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();

        return shoppingCartMapper.checkMyCart(currentUserCart);
    }

    //清空购物车
    @Override
    public void cleanCart() {
        //获取当前用户的id：
        ShoppingCart currentUserCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        shoppingCartMapper.cleanMyCart(currentUserCart);
    }


}
