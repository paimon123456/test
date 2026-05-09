package com.example.drug.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.drug.entity.SysRole;
import com.example.drug.entity.SysRolePerm;
import com.example.drug.entity.SysPermission;
import com.example.drug.mapper.SysRoleMapper;
import com.example.drug.mapper.SysRolePermMapper;
import com.example.drug.mapper.SysPermissionMapper;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public Result list() {
        List<SysRole> roles = sysRoleMapper.selectList(null);
        return Result.success(roles);
    }

    @PostMapping("/add")
    public Result add(@RequestBody SysRole role) {
        try {
            if (role.getRoleId() == null || role.getRoleId().isEmpty()) {
                role.setRoleId(UUID.randomUUID().toString().replace("-", ""));
            }
            sysRoleMapper.insert(role);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.fail("添加失败：" + e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result update(@RequestBody SysRole role) {
        try {
            sysRoleMapper.updateById(role);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.fail("修改失败：" + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        try {
            sysRoleMapper.deleteById(id);
            // 同时删除角色关联的权限
            QueryWrapper<SysRolePerm> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", id);
            sysRolePermMapper.delete(queryWrapper);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail("删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/assign-permissions")
    public Result assignPermissions(@RequestBody SysRolePerm rolePerm) {
        try {
            // 先删除该角色原有的权限
            QueryWrapper<SysRolePerm> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", rolePerm.getRoleId());
            sysRolePermMapper.delete(queryWrapper);
            
            // 添加新权限
            if (rolePerm.getPermId() != null) {
                rolePerm.setId(UUID.randomUUID().toString().replace("-", ""));
                sysRolePermMapper.insert(rolePerm);
            }
            return Result.success("权限分配成功");
        } catch (Exception e) {
            return Result.fail("权限分配失败：" + e.getMessage());
        }
    }

    @GetMapping("/permissions/{roleId}")
    public Result getPermissions(@PathVariable String roleId) {
        try {
            QueryWrapper<SysRolePerm> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId);
            List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(queryWrapper);
            return Result.success(rolePerms);
        } catch (Exception e) {
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/all-permissions")
    public Result getAllPermissions() {
        List<SysPermission> permissions = sysPermissionMapper.selectList(null);
        return Result.success(permissions);
    }
}
