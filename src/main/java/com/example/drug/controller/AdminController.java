package com.example.drug.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.drug.entity.SysRole;
import com.example.drug.entity.SysUser;
import com.example.drug.entity.SysUserRole;
import com.example.drug.mapper.SysRoleMapper;
import com.example.drug.mapper.SysUserMapper;
import com.example.drug.mapper.SysUserRoleMapper;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @PostMapping("/login")
    public Result login(String username, String password, HttpSession session) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", password);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        
        if (user != null) {
            // 检查账号是否被禁用
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.fail("账号已被禁用");
            }
            
            // 通过 sys_user_role 关联表查询用户角色
            QueryWrapper<SysUserRole> userRoleWrapper = new QueryWrapper<>();
            userRoleWrapper.eq("user_id", user.getUserId());
            SysUserRole userRole = sysUserRoleMapper.selectOne(userRoleWrapper);
            
            System.out.println("========== 登录调试信息 ==========");
            System.out.println("用户ID: " + user.getUserId());
            System.out.println("用户名: " + user.getUsername());
            System.out.println("sys_user表中的role字段: " + user.getRole());
            System.out.println("sys_user_role查询结果: " + (userRole != null ? "找到" : "未找到"));
            
            String roleName = "";
            if (userRole != null) {
                System.out.println("userRole的roleId: " + userRole.getRoleId());
                // 根据 role_id 查询角色名称
                SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
                if (role != null) {
                    roleName = role.getRoleName();
                    System.out.println("从sys_role表查到的角色名称: " + roleName);
                } else {
                    System.out.println("警告: 未找到role_id=" + userRole.getRoleId() + "的角色");
                    // 备选方案：使用 sys_user 表中的 role 字段
                    roleName = user.getRole() != null ? user.getRole() : "";
                }
            } else {
                // 如果没有分配角色，使用 sys_user 表中的 role 字段作为备选
                roleName = user.getRole() != null ? user.getRole() : "";
                System.out.println("使用sys_user表中的role字段: " + roleName);
            }
            
            // 确保角色名称不为空，如果为空则使用 sys_user.role
            if (roleName == null || roleName.trim().isEmpty()) {
                roleName = user.getRole() != null ? user.getRole() : "未知角色";
                System.out.println("角色名称为空，使用备选值: " + roleName);
            }
            System.out.println("最终返回的角色: " + roleName);
            System.out.println("====================================");
            
            // 将用户ID、用户名和角色存入session
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getUsername());
            session.setAttribute("userRole", roleName);
            
            // 返回用户信息和角色
            java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("role", roleName);
            userInfo.put("phone", user.getPhone());
            
            return Result.success(userInfo);
        }
        return Result.fail("账号或密码错误");
    }

    @PostMapping("/register")
    public Result register(@RequestBody SysUser user) {
        try {
            // 检查用户名是否已存在
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            if (sysUserMapper.selectOne(queryWrapper) != null) {
                return Result.fail("用户名已存在");
            }
            // 设置用户ID
            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                user.setUserId(UUID.randomUUID().toString().replace("-", ""));
            }
            user.setStatus(1); // 默认启用
            // 设置默认角色
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("药师"); // 默认角色为药师
            }
            sysUserMapper.insert(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.fail("注册失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list() {
        java.util.List<SysUser> users = sysUserMapper.selectList(null);
        System.out.println("========== 用户列表查询 ==========");
        System.out.println("数据库中用户总数: " + users.size());
        for (SysUser user : users) {
            System.out.println("用户ID: " + user.getUserId() + ", 用户名: " + user.getUsername() + ", 角色: " + user.getRole() + ", 状态: " + user.getStatus());
        }
        System.out.println("====================================");
        return Result.success(users);
    }

    @PostMapping("/update")
    public Result update(@RequestBody SysUser user) {
        try {
            sysUserMapper.updateById(user);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.fail("修改失败：" + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        try {
            sysUserMapper.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/status/{id}/{status}")
    public Result status(@PathVariable String id, @PathVariable Integer status) {
        SysUser user = new SysUser();
        user.setUserId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
        return Result.success("状态修改成功");
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody SysUser user) {
        SysUser existUser = sysUserMapper.selectById(user.getUserId());
        if (existUser == null) {
            return Result.fail("账号不存在");
        }
        existUser.setPassword(user.getPassword());
        sysUserMapper.updateById(existUser);
        return Result.success("密码重置成功");
    }

    @PostMapping("/assign-role")
    public Result assignRole(@RequestBody SysUserRole userRole) {
        try {
            // 先删除原有角色
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userRole.getUserId());
            sysUserRoleMapper.delete(queryWrapper);
            
            // 添加新角色
            if (userRole.getRoleId() != null) {
                userRole.setId(UUID.randomUUID().toString().replace("-", ""));
                sysUserRoleMapper.insert(userRole);
            }
            return Result.success("角色分配成功");
        } catch (Exception e) {
            return Result.fail("角色分配失败：" + e.getMessage());
        }
    }
}
