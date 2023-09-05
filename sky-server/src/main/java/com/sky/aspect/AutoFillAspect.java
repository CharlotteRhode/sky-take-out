package com.sky.aspect;


import com.aliyun.oss.common.utils.IniEditor;
import com.sky.Annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;


//公共地段的自动填充 - 切面类
@Slf4j
@Aspect
@Component
public class AutoFillAspect {



    @Before("execution(* com.sky.mapper.*.*(..)) && @annotation(autoFill)")
   public void autoFillProperty(JoinPoint joinPoint, AutoFill autoFill) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //获取原始方法传入的参数，获取第一个参数
        Object[] args = joinPoint.getArgs();
        if (ObjectUtils.isEmpty(args)){
            return;
        }
        Object obj = args[0];
        //通过反射的方式获取到它的方法（共4个）
        Method setCreateTime = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
        Method setUpdateTime = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

        Method setCreateUser = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
        Method setUpdateUser = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        //获取注解对应的value属性
        OperationType value = autoFill.value();
        //如果是insert, 就为4个属性赋值
        if (value.equals(OperationType.INSERT)){
            setCreateTime.invoke(obj,LocalDateTime.now());
            setUpdateTime.invoke(obj,LocalDateTime.now());
            setCreateUser.invoke(obj, BaseContext.getCurrentId());
            setUpdateUser.invoke(obj,BaseContext.getCurrentId());
        }
        //如果是update, 就为2个属性赋值
        if (value.equals(OperationType.UPDATE)){
            setUpdateTime.invoke(obj,LocalDateTime.now());
            setUpdateUser.invoke(obj,BaseContext.getCurrentId());
        }


   }





}
