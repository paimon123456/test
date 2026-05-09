package com.example.drug.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 【统一】角色权限关联实体
 * 映射到 sys_role_perm 表
 */
@Data
@TableName("sys_role_perm")
public class SysRolePerm implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String roleId;
    private String permId;
}
