package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    //捕获其他异常
    @ExceptionHandler
    public Result exceptionHandler(Exception ex){
        ex.printStackTrace();
        return Result.error(MessageConstant.UNKNOWN_ERROR);

    }




    //当用户输入的数据 违反数据库unique条件（数据重复）时，抛出此异常
    @ExceptionHandler
    public Result exceptionHandler(DuplicateKeyException ex){
        log.error("异常信息：{}", ex.getMessage());

        String errorMessage = MessageConstant.UNKNOWN_ERROR;

        String messageReceived = ex.getCause().getMessage();
        if (StringUtils.hasLength(messageReceived)){
            String[] strings = messageReceived.split(" ");
            errorMessage = strings[2] + " is repeated.";
        }

        return Result.error(errorMessage);
    }



}
