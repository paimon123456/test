package com.example.drug.entity.purchase;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购订单明细实体 (对应 purchase_item 表)
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
    // 批号
    private String batchNo;
    // 生产日期
    private Date productionDate;
    // 有效期
    private Date expiryDate;
    // 状态
    private String status;
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 规格
    @TableField(exist = false)
    private String specification;
}
