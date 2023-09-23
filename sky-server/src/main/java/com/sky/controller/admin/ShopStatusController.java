package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopStatusController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "open/close")
public class ShopStatusController {


    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    //设置营业状态：
    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status) {
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    //查询（显示）营业状态-管理端：
    @GetMapping("/status")
    public Result<Integer> showShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }


}
