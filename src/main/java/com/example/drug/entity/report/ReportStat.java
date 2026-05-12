package com.example.drug.entity.report;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("report_stat")
public class ReportStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String reportId;

    private String reportType;

    private String cycle;

    private String data;

    private String operator;

    private Date createTime;
}
