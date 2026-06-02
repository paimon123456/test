package com.example.drug.entity.price;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("price_alert_log")
public class PriceAlertLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String alertId;

    private String drugId;

    private String alertType;

    private String alertLevel;

    private String alertContent;

    private BigDecimal currentPurchasePrice;

    private BigDecimal currentRetailPrice;

    private BigDecimal thresholdPercent;

    private String handleStatus;

    private String handler;

    private Date handleTime;

    private String handleRemark;

    private Date createTime;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String specification;
}
