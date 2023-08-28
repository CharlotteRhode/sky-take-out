package com.sky;


import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;


public class test {

    @Test
    public void MD5Test(){
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
    }




}
