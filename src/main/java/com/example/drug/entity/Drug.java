package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 【统一】药品基础信息实体
 * 映射到 drug_info 表
 */
@Data
@TableName("drug_info")
public class Drug {
    // 药品ID
    @TableId(type = IdType.INPUT)
    private String drugId;
    
    // 药品名称
    private String drugName;
    
    // 通用名
    private String genericName;
    
    // 规格
    private String specification;
    
    // 单位
    private String unit;
    
    // 生产厂家
    private String manufacturer;
    
    // 批准文号
    private String approvalNo;
    
    // 分类
    private String category;
    
    // 医保类型
    private String medicalInsurance;
    
    // 采购价
    private BigDecimal purchasePrice;
    
    // 零售价
    private BigDecimal retailPrice;
    
    // 状态 1在售 0停售
    private Integer status;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
}
