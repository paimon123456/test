package com.example.drug.dto;

import lombok.Data;

/**
 * 库存盘点DTO
 */
@Data
public class InventoryCheckDTO {
    // 盘点类型：全盘/抽盘
    private String checkType;
    // 盘点人ID
    private String checkerId;
    // 药品ID列表（抽盘时使用）
    private java.util.List<String> drugIds;
    // 仓库ID（全盘时使用）
    private String warehouseId;
}
