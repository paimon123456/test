package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 【统一】用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {}
