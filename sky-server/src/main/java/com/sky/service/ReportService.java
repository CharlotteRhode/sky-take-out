package com.sky.service;


import com.sky.vo.TurnoverReportVO;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

public interface ReportService {

    //营业额图表
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);
}
