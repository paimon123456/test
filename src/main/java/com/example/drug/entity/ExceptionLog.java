package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("exception_log")
public class ExceptionLog {
    @TableId(type = IdType.INPUT)
    private String exceptionId;
    private String exceptionType;
    private String errorMessage;
    private String stackTrace;
    private String requestUrl;
    private String requestMethod;
    private String userId;
    private String username;
    private String ip;
    private Date createTime;
}
