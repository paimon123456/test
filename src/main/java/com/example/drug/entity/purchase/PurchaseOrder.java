package com.example.drug.entity.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购单主表实体 (对应 purchase_order 表)
 */
@Data
@TableName("purchase_order")
public class PurchaseOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    // 采购单号
    @TableId
    private String orderId;
    // 采购单显示编号
    private String orderNo;
    // 供应商ID
    private String supplierId;
    // 目标仓库ID
    private String warehouseId;
    // 制单人ID
    private String operatorId;
    // 审核人ID
    private String auditorId;
    // 总数量
    private Integer totalQuantity;
    // 总金额
    private BigDecimal totalAmount;
    // 优惠金额
    private BigDecimal discountAmount;
    // 已付款金额
    private BigDecimal paidAmount;
    // 状态：待审核/已通过/已驳回/已入库/已完成
    private String status;
    // 期望到货日期
    private Date expectedDate;
    // 实际到货日期
    private Date deliveryDate;
    // 审核时间
    private Date auditTime;
    // 备注
    private String remark;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

    // ========== 扩展字段 ==========
    // 供应商名称
    @TableField(exist = false)
    private String supplierName;
    // 仓库名称
    @TableField(exist = false)
    private String warehouseName;
    // 制单人姓名
    @TableField(exist = false)
    private String operatorName;
    // 审核人姓名
    @TableField(exist = false)
    private String auditorName;
    // 采购明细列表
    @TableField(exist = false)
    private List<PurchaseItem> items;
}
