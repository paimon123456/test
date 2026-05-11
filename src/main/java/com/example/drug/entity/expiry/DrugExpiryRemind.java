package com.example.drug.entity.expiry;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 【模块8】效期管理-效期提醒实体 (对应 drug_expiry_remind 表)
 */
@Data
@TableName("drug_expiry_remind")
public class DrugExpiryRemind implements Serializable {
    private static final long serialVersionUID = 1L;

    // 提醒ID
    @TableId
    private String remindId;
    // 库存ID
    private String inventoryId;
    // 药品ID
    private String drugId;
    // 预警天数
    private Integer thresholdDays;
    // 剩余有效期天数
    private Integer remainDays;
    // 状态 未处理/已处理/已忽略
    private String status;
    // 提醒时间
    private Date remindTime;

    // 扩展字段（关联查询）- 不存在于数据库表中
    @TableField(exist = false)
    private String drugName;
    @TableField(exist = false)
    private String specification;
    @TableField(exist = false)
    private String batchNo;
    @TableField(exist = false)
    private Date expiryDate;
    @TableField(exist = false)
    private Integer stockNum;
}
