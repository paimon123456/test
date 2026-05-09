package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 【统一】角色实体
 * 映射到 sys_role 表
 */
@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.INPUT)
    private String roleId;
    private String roleName;
    private String description;
}
