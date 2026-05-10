package com.example.drug.entity.sales;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售明细实体 (对应 sales_item 表)
 */
@Data
public class SalesItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // 明细ID
    private String itemId;
    // 销售单号
    private String orderId;
    // 药品ID
    private String drugId;
    // 批号
    private String batchNo;
    // 销售数量
    private Integer saleNum;
    // 销售单价
    private BigDecimal salePrice;
    
    // ========== 扩展字段（关联查询）==========
    // 药品名称
    private String drugName;
    // 药品规格
    private String specification;
    // 小计金额
    private BigDecimal subtotal;
}
