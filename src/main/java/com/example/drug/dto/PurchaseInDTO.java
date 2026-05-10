package com.example.drug.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 药品入库DTO
 */
@Data
public class PurchaseInDTO {
    // 采购单号
    private String orderId;
    // 操作人ID
    private String operatorId;
    // 入库时间
    private Date inTime;
    
    // 入库明细列表
    private List<PurchaseInItemDTO> items;
    
    @Data
    public static class PurchaseInItemDTO {
        // 药品ID
        private String drugId;
        // 采购数量
        private Integer purchaseNum;
        // 采购单价
        private java.math.BigDecimal purchasePrice;
        // 批号
        private String batchNo;
        // 生产日期
        private Date productionDate;
        // 有效期
        private Date expiryDate;
        // 仓库ID
        private String warehouseId;
        // 货架位置
        private String location;
    }
}
