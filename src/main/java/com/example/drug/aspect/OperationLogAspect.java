package com.example.drug.aspect;

import com.example.drug.common.IpUtil;
import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.OperationLog;
import com.example.drug.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    // 模块名映射
    private static final Map<String, String> MODULE_MAP = new HashMap<>();
    static {
        MODULE_MAP.put("AdminController", "用户管理");
        MODULE_MAP.put("RoleController", "角色管理");
        MODULE_MAP.put("PermissionController", "权限管理");
        MODULE_MAP.put("DrugController", "药品管理");
        MODULE_MAP.put("SupplierManagementController", "供应商管理");
        MODULE_MAP.put("PurchaseManagementController", "采购管理");
        MODULE_MAP.put("SalesManagementController", "销售管理");
        MODULE_MAP.put("InventoryController", "库存管理");
        MODULE_MAP.put("InventoryManagementController", "库存管理");
        MODULE_MAP.put("InventoryAlertController", "库存管理");
        MODULE_MAP.put("InventoryLogController", "库存管理");
        MODULE_MAP.put("WarehouseController", "仓库管理");
        MODULE_MAP.put("WarehouseLocationController", "仓库管理");
        MODULE_MAP.put("WarehouseTransferController", "仓库管理");
        MODULE_MAP.put("PriceController", "价格管理");
        MODULE_MAP.put("ReportController", "报表统计");
        MODULE_MAP.put("DrugExpiryRemindController", "效期管理");
        MODULE_MAP.put("DrugScrapController", "效期管理");
        MODULE_MAP.put("OperationLogController", "系统日志");
        MODULE_MAP.put("ExceptionLogController", "系统日志");
    }

    @Pointcut("execution(* com.example.drug.controller.*Controller.*(..))")
    public void controllerPointcut() {}

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            String simpleName = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String moduleName = MODULE_MAP.getOrDefault(simpleName, simpleName);
            String methodName = joinPoint.getSignature().getName();

            OperationLog log = new OperationLog();
            log.setLogId(UUIDUtil.getUUID());
            log.setModule(moduleName);
            log.setType(resolveOperationType(methodName));
            log.setContent(buildContent(methodName, joinPoint.getArgs()));
            log.setResult("成功");
            log.setCreateTime(new Date());

            setUserInfo(log);
            operationLogService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        try {
            String simpleName = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String moduleName = MODULE_MAP.getOrDefault(simpleName, simpleName);
            String methodName = joinPoint.getSignature().getName();

            OperationLog log = new OperationLog();
            log.setLogId(UUIDUtil.getUUID());
            log.setModule(moduleName);
            log.setType(resolveOperationType(methodName));
            log.setContent(buildContent(methodName, joinPoint.getArgs()) + " [失败原因: " + (ex.getMessage() != null ? ex.getMessage() : ex.toString()) + "]");
            log.setResult("失败");
            log.setCreateTime(new Date());

            setUserInfo(log);
            operationLogService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserInfo(OperationLog log) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setIp(IpUtil.getClientIp(request));
            Object userId = request.getSession().getAttribute("userId");
            Object username = request.getSession().getAttribute("userName");
            log.setUserId(userId != null ? userId.toString() : "");
            log.setUsername(username != null ? username.toString() : "未知用户");
        }
    }

    /**
     * 根据方法名解析操作类型
     */
    private String resolveOperationType(String methodName) {
        if (methodName.contains("save") || methodName.contains("add") || methodName.contains("create") || methodName.contains("insert")) {
            return "新增";
        } else if (methodName.contains("update") || methodName.contains("edit") || methodName.contains("modify")) {
            return "修改";
        } else if (methodName.contains("delete") || methodName.contains("remove") || methodName.contains("clear")) {
            return "删除";
        } else if (methodName.contains("export") || methodName.contains("download")) {
            return "导出";
        } else if (methodName.contains("import") || methodName.contains("upload")) {
            return "导入";
        } else if (methodName.contains("audit") || methodName.contains("approve") || methodName.contains("reject")) {
            return "审核";
        } else if (methodName.contains("login")) {
            return "登录";
        } else if (methodName.contains("logout")) {
            return "登出";
        }
        return "查询";
    }

    /**
     * 构建操作内容描述
     */
    private String buildContent(String methodName, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("操作: ").append(methodName);
        if (args != null && args.length > 0) {
            sb.append(" | 参数: ");
            for (int i = 0; i < args.length && i < 5; i++) {
                if (args[i] == null) continue;
                // 过滤掉 Request/Response/HttpSession 等框架对象
                String className = args[i].getClass().getName();
                if (className.startsWith("jakarta.") || className.startsWith("org.springframework.") 
                    || className.startsWith("javax.")) continue;
                String val = args[i].toString();
                if (val.length() > 200) val = val.substring(0, 200) + "...";
                sb.append(val).append("; ");
            }
        }
        return sb.toString();
    }
}
