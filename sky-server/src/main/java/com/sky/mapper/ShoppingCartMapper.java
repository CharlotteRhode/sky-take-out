package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    //shopping cart里有还是没有？
    List<ShoppingCart> checkMyCart(ShoppingCart myCart);


    //更新->购物车商品数量： 根据这条item的id:
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateItemNumber(ShoppingCart item);


    //往数据库里存储购物车新增的数据：
    @Insert("insert into shopping_cart (name,image,user_id, dish_id, dish_flavor, setmeal_id,number,amount," +
            "create_time) values (#{name},#{image},#{userId},#{dishId},#{dishFlavor},#{setmealId},#{number},#{amount},#{createTime}) ")
    void insert(ShoppingCart myCart);


    //清空购物车
    @Delete("delete from shopping_cart where user_id = #{userId} ")
    void cleanMyCart(ShoppingCart currentUserCart);
}
