package com.example.drug.entity.inventory;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 【模块5】库存管理-药品库存实体 (对应 drug_inventory 表)
 */
@Data
@TableName("drug_inventory")
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    // 库存ID
    @TableId
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

    // 动态计算的状态（用于前端显示）- 不存在于数据库表中
    @TableField(exist = false)
    private String dynamicStatus;

    // 剩余有效期天数（由SQL计算）- 不存在于数据库表中
    @TableField(exist = false)
    private Integer remainDays;

    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 药品名称
    @TableField(exist = false)
    private String drugName;
    // 药品规格
    @TableField(exist = false)
    private String specification;
    // 生产厂家
    @TableField(exist = false)
    private String manufacturer;
    // 仓库名称
    @TableField(exist = false)
    private String warehouseName;
    // 库区
    @TableField(exist = false)
    private String zone;
    // 货架
    @TableField(exist = false)
    private String shelf;
    // 层
    @TableField(exist = false)
    private String level;
}
