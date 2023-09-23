package com.sky.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.UserNotLoginException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WECHAT_LOGIN_URL= "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;



    //wechat login
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //1. 调用微信的接口，登陆 - httpC
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authentication_code");


        String result = HttpClientUtil.doGet(WECHAT_LOGIN_URL, map);
        log.info("微信登陆完成，结果是：{}", result);

        if (!StringUtils.hasLength(result)){
            throw new UserNotLoginException(MessageConstant.LOGIN_FAILED);
        }

        //use fastjson -> get openid:
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.getString("openid");//微信用户的唯一标识

        if (!StringUtils.hasLength(openid)){
            throw new UserNotLoginException(MessageConstant.LOGIN_FAILED);
        }



        //2.如果是第一次访问小程序，自动注册(insert into user)
        //显查询user表里有没有这个人的openid：
        User user = userMapper.selectByOpenid(openid);

        if (user == null){ //此人第一次登陆，自动注册
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }



        //3. 返回
        return user;
    }







}
