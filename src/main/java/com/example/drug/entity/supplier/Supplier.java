package com.example.drug.entity.supplier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 供应商档案实体 (对应 supplier 表)
 */
@Data
@TableName("supplier")
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

    // 供应商ID
    @TableId
    private String supplierId;
    // 供应商编码
    private String supplierCode;
    // 供应商名称
    private String supplierName;
    // 联系人
    private String contact;
    // 联系电话
    private String phone;
    // 邮箱
    private String email;
    // 地址
    private String address;
    // 营业执照编号
    private String businessLicense;
    // 营业执照有效期
    private Date licenseExpiryDate;
    // GSP证书编号
    private String gspCertNo;
    // GSP证书有效期
    private Date gspExpiryDate;
    // 药品经营许可证编号
    private String pharmaLicense;
    // 药品经营许可证有效期
    private Date pharmaExpiryDate;
    // 资质状态：正常/近效期/过期/缺失
    private String qualificationStatus;
    // 合作状态：合作中/暂停/终止
    private String cooperationStatus;
    // 信用等级：A/B/C/D
    private String creditLevel;
    // 备注
    private String remark;
    // 状态：1启用 0停用
    private Integer status;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

    // ========== 扩展字段 ==========
    // 距资质过期天数
    @TableField(exist = false)
    private Integer daysToExpire;
    // 是否即将过期（30天内）
    @TableField(exist = false)
    private Boolean nearExpiry;
}
