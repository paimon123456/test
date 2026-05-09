package com.example.drug.entity.warehouse;

import lombok.Data;
import java.io.Serializable;

/**
 * 【模块7】仓库管理-库位实体
 */
@Data
public class WarehouseLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    // 库位ID (主键)
    private String locId;
    // 库位编码 (显示用，如 A01-01)
    private String locCode;
    // 所属仓库ID
    private String warehouseId;
    // 库区
    private String zone;
    // 货架号
    private String shelf;
    // 层号
    private String level;
    // 状态 空闲/占用/锁定
    private String status;

    // 扩展字段（关联查询）
    private String warehouseName;
}
