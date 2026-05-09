package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 【统一】权限实体
 * 映射到 sys_permission 表
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    @TableId(type = IdType.INPUT)
    private String permId;
    private String permName;
    private String permCode;
}
