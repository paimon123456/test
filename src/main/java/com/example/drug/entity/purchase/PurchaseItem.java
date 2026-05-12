package com.example.drug.entity.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购单明细实体 (对应 purchase_item 表)
 */
@Data
@TableName("purchase_item")
public class PurchaseItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // 明细ID
    @TableId
    private String itemId;
    // 采购单号
    private String orderId;
    // 药品ID
    private String drugId;
    // 采购数量
    private Integer purchaseNum;
    // 采购单价
    private BigDecimal purchasePrice;
    // 小计金额
    private BigDecimal subtotal;
    // 已入库数量
    private Integer receivedNum;
    // 待入库数量
    private Integer pendingNum;
    // 批号（入库时填）
    private String batchNo;
    // 生产日期（入库时填）
    private String productionDate;
    // 有效期（入库时填）
    private String expiryDate;
    // 状态：待入库/部分入库/已完成
    private String status;

    // ========== 扩展字段 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 药品规格
    @TableField(exist = false)
    private String specification;
    // 单位
    @TableField(exist = false)
    private String unit;
}
