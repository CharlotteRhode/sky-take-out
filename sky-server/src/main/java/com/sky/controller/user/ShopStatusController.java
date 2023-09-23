package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopStatusController")
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "open/close")
public class ShopStatusController {


    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";



    //查询（显示）营业状态-管理端：
    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result<Integer> showShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }


}
