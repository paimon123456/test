package com.example.drug.entity.warehouse;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 【模块7】仓库管理-移库记录实体 (对应 warehouse_transfer 表)
 */
@Data
@TableName("warehouse_transfer")
public class WarehouseTransfer implements Serializable {
    private static final long serialVersionUID = 1L;

    // 移库ID
    @TableId
    private String transferId;
    // 移库单号
    private String transferNo;
    // 原仓库ID
    private String srcWareId;
    // 目标仓库ID
    private String destWareId;
    // 库存ID
    private String inventoryId;
    // 移库数量
    private Integer transferNum;
    // 状态 申请中/已审核/已完成/已驳回
    private String status;

    // 扩展字段（关联查询）- 不存在于数据库表中
    @TableField(exist = false)
    private String srcWareName;
    @TableField(exist = false)
    private String destWareName;
}
