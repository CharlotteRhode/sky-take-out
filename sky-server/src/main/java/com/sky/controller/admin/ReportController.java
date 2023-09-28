package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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






}
