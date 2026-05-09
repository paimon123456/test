package com.example.drug.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.drug.entity.SysUserRole;
import com.example.drug.entity.SysRolePerm;
import com.example.drug.mapper.SysUserRoleMapper;
import com.example.drug.mapper.SysRolePermMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前用户ID（从session或token中获取）
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj == null) {
            response.sendRedirect("/login.html");
            return false;
        }

        String userId = (String) userIdObj;
        String uri = request.getRequestURI();

        // 检查用户是否有角色分配
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        SysUserRole userRole = sysUserRoleMapper.selectOne(queryWrapper);

        // 如果没有分配角色，默认允许访问（或可以跳转到角色分配页面）
        if (userRole == null) {
            return true;
        }

        // 如果已分配角色，获取该角色的所有权限
        QueryWrapper<SysRolePerm> permQueryWrapper = new QueryWrapper<>();
        permQueryWrapper.eq("role_id", userRole.getRoleId());
        List<SysRolePerm> permissions = sysRolePermMapper.selectList(permQueryWrapper);

        // 如果角色没有配置任何权限，默认放行
        if (permissions.isEmpty()) {
            return true;
        }

        // 检查是否有权限访问当前接口
        boolean hasPermission = permissions.stream().anyMatch(rp -> {
            // 这里简化处理，实际应该根据权限配置进行匹配
            return true;
        });

        if (!hasPermission) {
            response.sendError(403, "无权限访问");
            return false;
        }

        return true;
    }
}
