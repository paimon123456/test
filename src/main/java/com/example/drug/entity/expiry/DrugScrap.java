package com.example.drug.entity.expiry;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 【模块8】效期管理-药品报废实体 (对应 drug_scrap 表)
 */
@Data
@TableName("drug_scrap")
public class DrugScrap implements Serializable {
    private static final long serialVersionUID = 1L;

    // 报废ID
    @TableId
    private String scrapId;
    // 报废单号
    private String scrapNo;
    // 库存ID
    private String inventoryId;
    // 药品ID
    private String drugId;
    // 报废数量
    private Integer scrapNum;
    // 报废原因
    private String reason;
    // 状态 申请中/已审核/已审批/已执行/已驳回
    private String status;

    // 扩展字段（关联查询）- 不存在于数据库表中
    @TableField(exist = false)
    private String drugName;
    @TableField(exist = false)
    private String specification;
    @TableField(exist = false)
    private String batchNo;
}
