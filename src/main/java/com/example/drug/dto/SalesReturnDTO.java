package com.example.drug.dto;

import lombok.Data;

/**
 * 销售退货DTO
 */
@Data
public class SalesReturnDTO {
    // 原销售单号
    private String originalOrderId;
    // 退货明细
    private java.util.List<ReturnItemDTO> items;
    // 退货原因
    private String returnReason;
    // 操作人ID
    private String operatorId;
    
    @Data
    public static class ReturnItemDTO {
        // 药品ID
        private String drugId;
        // 批号
        private String batchNo;
        // 退货数量
        private Integer returnNum;
    }
}
