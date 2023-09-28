package com.sky.service;


import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

public interface ReportService {

    //营业额图表
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);


    //用户图表
    UserReportVO userStatistics(LocalDate begin, LocalDate end);


    //订单统计图表
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);


    //销量统计
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
