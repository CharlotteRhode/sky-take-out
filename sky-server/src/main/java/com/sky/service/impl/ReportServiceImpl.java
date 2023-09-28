package com.sky.service.impl;

import com.sky.dto.TurnOverReportDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;


    //营业额图表
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end)  {
        //获取日期列表datelist：
        List<String> dateList = getDateList(begin, end);


        //获取指定日期范围内的营业额turnover list:
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<TurnOverReportDTO> turnOverList = ordersMapper.checkTurnOverAmount(beginTime,endTime);

        //当天营业额为0怎么办？把原始结果->编程map(key:date, value:turnover)
        Map<String, BigDecimal> map = turnOverList.stream().collect(Collectors.toMap(TurnOverReportDTO::getOrderDate, TurnOverReportDTO::getOrderMoney));

        List<BigDecimal> turnoverList = dateList.stream().map(eachDate->{
            return map.get(eachDate) == null ? new BigDecimal("0") : map.get(eachDate);
        }).collect(Collectors.toList());

        //封装数据并返回：
        return new TurnoverReportVO(dateList.toString(),turnoverList.toString());


    }



    private static List<String> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = begin.datesUntil(end.plusDays(1)).toList();

        //change LocalDate type -> to String type:
       return  dateList.stream().map(i->{
          return i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }).collect(Collectors.toList());
    }




}
