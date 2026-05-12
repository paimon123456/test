package com.example.drug.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购明细DTO
 */
@Data
public class PurchaseItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private String drugId;
    private String drugName;  // 药品名称，用于按名称查询
    private Integer purchaseNum;
    private BigDecimal purchasePrice;
    private String batchNo;
    private String productionDate;
    private String expiryDate;
    private String warehouseId;
    private String location;
}
