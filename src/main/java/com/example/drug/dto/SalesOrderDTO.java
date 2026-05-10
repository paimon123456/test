package com.example.drug.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 销售开单DTO
 */
@Data
public class SalesOrderDTO {
    // 会员ID（可选）
    private String memberId;
    // 收银员ID
    private String cashierId;
    // 支付方式：现金/微信/支付宝/医保/银行卡
    private String payType;
    // 优惠金额
    private java.math.BigDecimal discount;
    // 积分抵扣
    private Integer pointsUsed;
    
    // 销售明细列表
    private List<SalesItemDTO> items;
    
    @Data
    public static class SalesItemDTO {
        // 药品ID
        private String drugId;
        // 销售数量
        private Integer saleNum;
        // 销售单价
        private java.math.BigDecimal salePrice;
    }
}
