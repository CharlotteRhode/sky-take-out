package com.sky.mapper;


import com.sky.dto.UserReportDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

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


    //统计每一天的新增用户数量
    List<UserReportDTO> countAddByCreateTime(LocalDateTime beginTime, LocalDateTime endTime);



    //获取用户总量 totalUserList
    //查询技术（begin之前）
    @Select("select count(*) from user where create_time < #{beginTime}")
    Integer countTotalUntilBegin(LocalDateTime beginTime);



    
}
