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
@TableName("drug_price")
public class DrugPrice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String priceId;

    private String drugId;

    private BigDecimal purchasePrice;

    private BigDecimal retailPrice;

    private BigDecimal memberPrice;

    private BigDecimal promoPrice;

    private Date promoStart;

    private Date promoEnd;

    private String operator;

    private Date updateTime;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String specification;
}
