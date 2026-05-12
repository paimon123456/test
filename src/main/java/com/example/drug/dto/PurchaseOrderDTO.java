package com.example.drug.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购订单DTO
 */
@Data
public class PurchaseOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String supplierId;
    private String warehouseId;
    private String operatorId;
    private Date expectedDate;
    private String remark;
    
    // 明细列表
    private java.util.List<PurchaseItemDTO> items;
}
