package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnOverReportDTO implements Serializable {

    private String orderDate;//订单日期
    private BigDecimal orderMoney;//营业额
}
