package com.example.drug.entity.inventory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购入库明细实体 (对应 purchase_in_item 表)
 */
@Data
@TableName("purchase_in_item")
public class PurchaseInItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // 入库明细ID
    @TableId
    private String inItemId;
    // 入库单号
    private String inId;
    // 采购明细ID
    private String itemId;
    // 药品ID
    private String drugId;
    // 批号
    private String batchNo;
    // 生产日期
    private Date productionDate;
    // 有效期
    private Date expiryDate;
    // 入库数量
    private Integer inNum;
    // 采购单价
    private BigDecimal purchasePrice;
    // 小计金额
    private BigDecimal subtotal;
    // 仓库ID
    private String warehouseId;
    // 库位
    private String location;
    // 操作人ID
    private String operatorId;
    // 创建时间
    private Date createTime;

    // ========== 扩展字段 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 药品规格
    @TableField(exist = false)
    private String specification;
    // 仓库名称
    @TableField(exist = false)
    private String warehouseName;
}
