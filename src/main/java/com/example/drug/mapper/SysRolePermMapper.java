package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.SysRolePerm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【统一】角色权限关联Mapper
 */
@Mapper
public interface SysRolePermMapper extends BaseMapper<SysRolePerm> {}
