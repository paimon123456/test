package com.example.drug.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.drug.entity.SysUser;
import com.example.drug.entity.SysUserRole;
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
            // 将用户ID和用户名存入session
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getUsername());
            return Result.success(user);
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
