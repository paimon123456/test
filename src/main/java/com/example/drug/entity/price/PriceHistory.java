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
@TableName("price_history")
public class PriceHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String historyId;

    private String drugId;

    private String priceType;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private BigDecimal changeRate;

    private String adjustType;

    private String batchNo;

    private Date effectiveDate;

    private String reason;

    private String operator;

    private Date createTime;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String specification;
}
