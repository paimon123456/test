package com.example.drug.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 药品报废入参DTO
 */
@Data
public class ScrapDTO {
    private String scrapId;

    @NotBlank(message = "库存不能为空")
    private String inventoryId;

    @NotBlank(message = "药品不能为空")
    private String drugId;

    @NotNull(message = "报废数量不能为空")
    private Integer scrapNum;

    @NotBlank(message = "报废原因不能为空")
    private String reason;
}
