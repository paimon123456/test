package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 【统一】用户实体
 * 映射到 sys_user 表
 */
@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.INPUT)
    private String userId;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private Integer status;
    private Date createTime;
}
