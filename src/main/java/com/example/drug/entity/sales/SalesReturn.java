package com.example.drug.entity.sales;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 销售退货单实体
 */
@Data
public class SalesReturn implements Serializable {
    private static final long serialVersionUID = 1L;

    // 退货单号
    private String returnId;
    // 原销售单号
    private String originalOrderId;
    // 药品ID
    private String drugId;
    // 批号
    private String batchNo;
    // 退货数量
    private Integer returnNum;
    // 退货原因
    private String returnReason;
    // 退款金额
    private java.math.BigDecimal refundAmount;
    // 退货状态：申请中/审核中/已完成/已驳回
    private String status;
    // 操作人ID
    private String operatorId;
    // 审核人ID
    private String auditorId;
    // 创建时间
    private Date createTime;
    // 审核时间
    private Date auditTime;
    
    // ========== 扩展字段（关联查询）==========
    // 药品名称
    private String drugName;
    // 操作人姓名
    private String operatorName;
    // 审核人姓名
    private String auditorName;
}
