package com.example.drug.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 仓库档案入参DTO
 */
@Data
public class WarehouseDTO {
    private String warehouseId;

    @NotBlank(message = "仓库名称不能为空")
    private String warehouseName;

    private String location;

    @NotBlank(message = "负责人不能为空")
    private String managerId;

    private Integer status;
}
