package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【统一】权限Mapper
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {}
