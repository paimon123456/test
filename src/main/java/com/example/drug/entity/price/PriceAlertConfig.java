package com.example.drug.entity.price;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("price_alert_config")
public class PriceAlertConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String configId;

    private String alertType;

    private BigDecimal thresholdPercent;

    private Integer enableStatus;

    private String remark;

    private Date createTime;

    private Date updateTime;
}
