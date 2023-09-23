package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    //查询此用户是否是第一次用ap：
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);


    //新增此用户（插入）
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into  user (openid,name,phone,sex,id_number,avatar,create_time) values (" +
            " #{openid}, #{name}, #{phone}, #{sex}, #{idNumber},#{avatar}, #{createTime})")
    void insert(User user);
}
