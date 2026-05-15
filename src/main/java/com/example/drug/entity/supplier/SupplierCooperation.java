package com.example.drug.entity.supplier;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商合作记录实体 (对应 supplier_cooperation 表)
 */
@Data
@TableName("supplier_cooperation")
public class SupplierCooperation implements Serializable {
    private static final long serialVersionUID = 1L;

    // 记录ID
    @TableId
    private String recordId;
    // 供应商ID
    private String supplierId;
    // 记录类型：对账/供货/问题/评价
    private String recordType;
    // 记录内容
    private String content;
    // 涉及金额
    private BigDecimal amount;
    // 操作人ID
    private String operatorId;
    // 创建时间
    private Date createTime;

    // ========== 扩展字段 ==========
    // 供应商名称
    @TableField(exist = false)
    private String supplierName;
    // 操作人姓名
    @TableField(exist = false)
    private String operatorName;
}
