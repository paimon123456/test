package com.example.drug.entity.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DailyReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String drugId;

    private String drugName;

    private String specification;

    private String unit;

    private Integer openingStock;

    private Integer purchaseIn;

    private Integer saleOut;

    private Integer returnIn;

    private Integer returnOut;

    private Integer closingStock;

    private BigDecimal purchaseAmount;

    private BigDecimal saleAmount;

    private BigDecimal profit;

    private String reportDate;
}
