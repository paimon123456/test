package com.example.drug.entity.inventory;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

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
    // 采购单号
    private String orderId;
    // 操作人ID
    private String operatorId;
    // 入库时间
    private Date inTime;
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 操作人姓名
    @TableField(exist = false)
    private String operatorName;
    // 供应商名称
    @TableField(exist = false)
    private String supplierName;
}
