package com.example.drug.entity.inventory;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 库存盘点单实体 (对应 inventory_check 表)
 */
@Data
@TableName("inventory_check")
public class InventoryCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    // 盘点单编号
    @TableId
    private String checkId;
    // 药品ID
    private String drugId;
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 系统库存数量
    private Integer systemStock;
    // 实际库存数量
    private Integer actualStock;
    // 差异数量
    private Integer diffNum;
    // 差异原因
    private String diffReason;
    // 盘点日期
    private Date checkDate;
    // 盘点人
    private String checkerId;
    // 审核人
    private String auditorId;
    // 审核状态：待审核/已通过/已驳回
    private String auditStatus;
    // 库存调整状态：未调整/已调整
    private String adjustStatus;
    // 创建时间
    private Date createTime;
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 盘点人姓名
    @TableField(exist = false)
    private String checkerName;
    // 审核人姓名
    @TableField(exist = false)
    private String auditorName;
}
