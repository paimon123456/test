package com.example.drug.entity.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class InventoryTurnover implements Serializable {
    private static final long serialVersionUID = 1L;

    private String drugId;

    private String drugName;

    private String specification;

    private Integer averageStock;

    private Integer totalSales;

    private Integer totalPurchase;

    private BigDecimal turnoverRate;

    private String turnoverDays;

    private String remark;
}
