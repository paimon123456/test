package com.example.drug.entity.warehouse;

import lombok.Data;
import java.io.Serializable;

/**
 * 【模块7】仓库管理-移库记录实体
 */
@Data
public class WarehouseTransfer implements Serializable {
    private static final long serialVersionUID = 1L;

    // 移库ID
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

    // 扩展字段（关联查询）
    private String srcWareName;
    private String destWareName;
}
