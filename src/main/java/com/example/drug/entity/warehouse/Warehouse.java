package com.example.drug.entity.warehouse;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 【模块7】仓库管理-仓库档案实体 (对应 warehouse 表)
 */
@Data
@TableName("warehouse")
public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    // 仓库ID (主键)
    private String warehouseId;
    // 仓库编码 (显示用，如 W001)
    private String warehouseCode;
    // 仓库名称
    private String warehouseName;
    // 仓库地址
    private String location;
    // 负责人ID
    private String managerId;
    // 状态 1启用 0禁用
    private Integer status;
}
