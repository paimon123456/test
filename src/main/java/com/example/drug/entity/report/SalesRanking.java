package com.example.drug.entity.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalesRanking implements Serializable {
    private static final long serialVersionUID = 1L;

    private String drugId;

    private String drugName;

    private String specification;

    private String unit;

    private Integer totalSalesNum;

    private BigDecimal totalSalesAmount;

    private Integer rank;

    private BigDecimal proportion;
}
