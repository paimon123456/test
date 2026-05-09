package com.example.drug.entity.inventory;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 【模块5】库存管理-药品库存实体 (对应 drug_inventory 表)
 */
@Data
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    // 库存ID
    private String inventoryId;
    // 药品ID
    private String drugId;
    // 批号
    private String batchNo;
    // 生产日期
    private Date productionDate;
    // 有效期
    private Date expiryDate;
    // 库存数量
    private Integer stockNum;
    // 仓库ID
    private String warehouseId;
    // 货架位置
    private String location;
    // 最低库存预警
    private Integer minStock;
    // 状态 正常/近效期/过期/锁定
    private String status;

    // 动态计算的状态（用于前端显示）
    private String dynamicStatus;

    // 剩余有效期天数（由SQL计算）
    private Integer remainDays;

    // ========== 扩展字段（关联查询）==========
    // 药品名称
    private String drugName;
    // 药品规格
    private String specification;
    // 生产厂家
    private String manufacturer;
    // 仓库名称
    private String warehouseName;
    // 库区
    private String zone;
    // 货架
    private String shelf;
    // 层
    private String level;
}
