package com.example.drug.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 移库操作入参DTO
 */
@Data
public class TransferDTO {
    private String transferId;

    @NotBlank(message = "原仓库不能为空")
    private String srcWareId;

    @NotBlank(message = "目标仓库不能为空")
    private String destWareId;

    @NotBlank(message = "库存不能为空")
    private String inventoryId;

    @NotNull(message = "移库数量不能为空")
    private Integer transferNum;
}
