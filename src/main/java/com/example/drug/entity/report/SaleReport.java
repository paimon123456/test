package com.example.drug.entity.report;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("sale_report")
public class SaleReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String id;

    private String reportId;

    private String drugId;

    private Integer saleNum;

    private BigDecimal saleMoney;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String drugName;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String specification;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String unit;
}
