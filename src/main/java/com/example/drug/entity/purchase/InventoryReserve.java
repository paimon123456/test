package com.example.drug.entity.purchase;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存预占实体 (对应 inventory_reserve 表)
 */
@Data
@TableName("inventory_reserve")
public class InventoryReserve implements Serializable {
    private static final long serialVersionUID = 1L;

    // 预占ID
    @TableId
    private String reserveId;
    // 关联单号
    private String orderId;
    // 单据类型：采购/销售/调拨
    private String orderType;
    // 药品ID
    private String drugId;
    // 预占数量
    private Integer reserveNum;
    // 状态：预占中/已确认/已释放/已取消
    private String status;
    // 预占过期时间
    private Date expireTime;
    // 操作人ID
    private String operatorId;
    // 创建时间
    private Date createTime;

    // ========== 扩展字段 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 预占类型描述
    @TableField(exist = false)
    private String orderTypeDesc;
}
