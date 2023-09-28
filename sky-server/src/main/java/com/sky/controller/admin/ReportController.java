package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    //营业额图表
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);
                return Result.success(turnoverReportVO);
    }

    //用户图表
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        UserReportVO userReportVO = reportService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }


    //订单统计图表
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        OrderReportVO orderReportVO = reportService.orderStatistics(begin,end);
        return Result.success(orderReportVO);
    }


    //销量统计
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
         SalesTop10ReportVO salesTop10ReportVO= reportService.getSalesTop10(begin,end);
        return Result.success(salesTop10ReportVO);
    }



    //excel报表导出
    @GetMapping("/export")
    public void exportData() throws IOException {
        reportService.exportData();
    }














}
