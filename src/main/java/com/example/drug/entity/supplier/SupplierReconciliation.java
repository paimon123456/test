package com.example.drug.entity.supplier;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商对账实体 (对应 supplier_reconciliation 表)
 */
@Data
@TableName("supplier_reconciliation")
public class SupplierReconciliation implements Serializable {
    private static final long serialVersionUID = 1L;

    // 账单ID
    @TableId
    private String billId;
    // 供应商ID
    private String supplierId;
    // 关联采购单号
    private String orderId;
    // 对账周期
    private String cycle;
    // 对账开始日期
    private Date startDate;
    // 对账结束日期
    private Date endDate;
    // 对账金额
    private BigDecimal totalAmount;
    // 已付款金额
    private BigDecimal paidAmount;
    // 未付款金额
    private BigDecimal unpaidAmount;
    // 状态：未对账/已对账/已付款/已核销
    private String status;
    // 对账时间
    private Date reconcileTime;
    // 付款时间
    private Date paidTime;
    // 备注
    private String remark;
    // 创建时间
    private Date createTime;

    // ========== 扩展字段 ==========
    // 供应商名称
    @TableField(exist = false)
    private String supplierName;
    // 供应商编码
    @TableField(exist = false)
    private String supplierCode;
}
