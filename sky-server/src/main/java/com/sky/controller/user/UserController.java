package com.sky.controller.user;


import com.sky.Annotation.AutoFill;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/user")
@Api(tags = "client-side: 用户管理接口")
public class UserController {


    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @ApiOperation("wechat login")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("wechat login: {}", userLoginDTO);


        //1. service
        User user = userService.login(userLoginDTO);
        //2.generate jwt
        Map<String, Object> map = new HashMap<>();
        map.put(JwtClaimsConstant.USER_ID, user.getId());

        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(), map);


        //3.return result 封装数据+返回：
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(user.getId());
        userLoginVO.setOpenid(user.getOpenid());
        userLoginVO.setToken(jwt);

        return Result.success(userLoginVO);
    }



}
