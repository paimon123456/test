package com.example.drug.entity.price;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PriceAdjust implements Serializable {
    private static final long serialVersionUID = 1L;

    private String adjustNo;

    private String adjustType;

    private Date effectiveDate;

    private String reason;

    private String status;

    private String operator;

    private Date createTime;

    private List<PriceAdjustItem> items;

    @Data
    public static class PriceAdjustItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private String drugId;

        private String priceType;

        private BigDecimal oldPrice;

        private BigDecimal newPrice;

        private BigDecimal changeRate;
    }
}
