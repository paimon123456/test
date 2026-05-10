package com.example.drug.entity.inventory;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 药品入库单实体 (对应 purchase_in 表)
 */
@Data
public class PurchaseIn implements Serializable {
    private static final long serialVersionUID = 1L;

    // 入库单号
    private String inId;
    // 采购单号
    private String orderId;
    // 操作人ID
    private String operatorId;
    // 入库时间
    private Date inTime;
    
    // ========== 扩展字段（关联查询）==========
    // 操作人姓名
    private String operatorName;
    // 供应商名称
    private String supplierName;
}
