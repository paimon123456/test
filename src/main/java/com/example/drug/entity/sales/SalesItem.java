package com.example.drug.entity.sales;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售明细实体 (对应 sales_item 表)
 */
@Data
@TableName("sales_item")
public class SalesItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // 明细ID
    @TableId
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
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 药品规格
    @TableField(exist = false)
    private String specification;
    // 小计金额
    @TableField(exist = false)
    private BigDecimal subtotal;
}
