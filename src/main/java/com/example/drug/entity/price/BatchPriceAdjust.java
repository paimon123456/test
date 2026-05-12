package com.example.drug.entity.price;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BatchPriceAdjust implements Serializable {
    private static final long serialVersionUID = 1L;

    private String batchNo;

    private String adjustType;

    private BigDecimal adjustRate;

    private String priceType;

    private Date effectiveDate;

    private String reason;

    private String operator;

    private Date createTime;

    private List<String> drugIds;

    private List<Result> items;

    @Data
    public static class Result implements Serializable {
        private static final long serialVersionUID = 1L;

        private String drugId;

        private String drugName;

        private BigDecimal oldPrice;

        private BigDecimal newPrice;

        private BigDecimal changeRate;

        private boolean success;

        private String message;
    }
}
