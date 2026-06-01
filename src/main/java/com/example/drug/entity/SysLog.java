package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志实体 (对应 sys_log 表)
 */
@Data
@TableName("sys_log")
public class SysLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String logId;
    
    // 操作模块
    private String module;
    
    // 操作类型
    private String type;
    
    // 操作人ID
    private String userId;
    
    // 操作人姓名
    private String username;
    
    // 操作内容（JSON格式）
    private String content;
    
    // IP地址
    private String ip;
    
    // 创建时间
    private Date createTime;
}
