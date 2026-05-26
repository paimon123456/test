package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_log")
public class OperationLog {
    @TableId(type = IdType.INPUT)
    private String logId;
    private String module;
    private String type;
    private String userId;
    private String username;
    private String content;
    private String result;
    private String ip;
    private Date createTime;
    
    // 兼容旧代码的字段
    @TableField(exist = false)
    private String adminName;
    @TableField(exist = false)
    private String operation;
    @TableField(exist = false)
    private String method;
    @TableField(exist = false)
    private String params;
}
