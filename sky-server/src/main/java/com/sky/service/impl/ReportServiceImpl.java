package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrderReportDTO;
import com.sky.dto.TurnOverReportDTO;
import com.sky.dto.UserReportDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;


    //营业额图表
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表datelist：
        List<String> dateList = getDateList(begin, end);


        //获取指定日期范围内的营业额turnover list:
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<TurnOverReportDTO> turnOverList = ordersMapper.checkTurnOverAmount(beginTime, endTime);

        //当天营业额为0怎么办？把原始结果->编程map(key:date, value:turnover)
        Map<String, BigDecimal> map = turnOverList.stream().collect(Collectors.toMap(TurnOverReportDTO::getOrderDate, TurnOverReportDTO::getOrderMoney));

        List<Object> turnoverList = dateList.stream().map(eachDate ->
                map.get(eachDate) == null ? 0 : map.get(eachDate)
        ).collect(Collectors.toList());

        //封装数据并返回：
        return new TurnoverReportVO(dateList.toString(), turnoverList.toString());


    }


    private static List<String> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = begin.datesUntil(end.plusDays(1)).toList();

        //change LocalDate type -> to String type:
        return dateList.stream().map(i -> {
            return i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }).collect(Collectors.toList());
    }


    //用户图表
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表 dateList
        List<String> dateList = getDateList(begin, end);

        //获取新增用户列表 newUserList
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<UserReportDTO> userReportDTOList = userMapper.countAddByCreateTime(beginTime, endTime);
        Map<String, Integer> map = userReportDTOList.stream().collect(Collectors.toMap(UserReportDTO::getCreateDate, UserReportDTO::getUserCount));

        List<Integer> newUserList = dateList.stream().map(i ->
                map.get(i) == null ? 0 : map.get(i)
        ).collect(Collectors.toList());


        //获取用户总量 totalUserList
        //查询技术（begin之前）
        Integer baseCount = userMapper.countTotalUntilBegin(beginTime);
        //累加
        ArrayList<Integer> totalUserList = new ArrayList<>();
        for (Integer i : newUserList) {

            baseCount += i;
            totalUserList.add(baseCount);
        }

        return new UserReportVO(dateList.toString(), newUserList.toString(), totalUserList.toString());

    }


    //订单统计图表
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {

        //获取日期列表 dateList
        List<String> dateList = getDateList(begin, end);

        //get 每日订单总数
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<OrderReportDTO> orderReportDTOS = ordersMapper.countOrderByOrderTime(beginTime, endTime);

        Map<String, Integer> map = orderReportDTOS.stream().collect(Collectors.toMap(OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount));
        List<Integer> orderCountList = dateList.stream().map(i -> map.get(i) == null ? 0 : map.get(i)).collect(Collectors.toList());


        //每日有效订单总数
        List<OrderReportDTO> validOrderReportDTOS = ordersMapper.countOrderByOrderTime(beginTime, endTime);

        Map<String, Integer> validMap = validOrderReportDTOS.stream().collect(Collectors.toMap(OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount));
        List<Integer> validOrderCountList = dateList.stream().map(i -> validMap.get(i) == null ? 0 : validMap.get(i)).collect(Collectors.toList());



        //总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();

        return new OrderReportVO(dateList.toString(),orderCountList.toString(), validOrderCountList.toString(), totalOrderCount, validOrderCount, orderCompletionRate );
    }

    //销量统计
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = ordersMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");


        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return new SalesTop10ReportVO(nameList, numberList);


    }


}
