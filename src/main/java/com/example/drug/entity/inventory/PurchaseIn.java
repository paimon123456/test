package com.example.drug.entity.inventory;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 药品入库单实体 (对应 purchase_in 表)
 */
@Data
@TableName("purchase_in")
public class PurchaseIn implements Serializable {
    private static final long serialVersionUID = 1L;

    // 入库单号
    @TableId
    private String inId;
    // 入库单显示编号
    private String inNo;
    // 采购单号
    private String orderId;
    // 供应商ID
    private String supplierId;
    // 操作人ID
    private String operatorId;
    // 入库仓库ID
    private String warehouseId;
    // 入库总金额
    private BigDecimal totalAmount;
    // 状态：待入库/入库中/已完成
    private String status;
    // 入库时间
    private Date inTime;
    // 备注
    private String remark;
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 操作人姓名
    @TableField(exist = false)
    private String operatorName;
    // 供应商名称
    @TableField(exist = false)
    private String supplierName;
    // 仓库名称
    @TableField(exist = false)
    private String warehouseName;
    // 入库明细列表
    @TableField(exist = false)
    private List<PurchaseInItem> items;
}
