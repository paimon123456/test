package com.example.drug.aspect;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.OperationLog;
import com.example.drug.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Pointcut("execution(* com.example.drug.controller.*Controller.*(..))")
    public void controllerPointcut() {}

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            OperationLog log = new OperationLog();
            log.setLogId(UUIDUtil.getUUID());
            log.setModule(joinPoint.getSignature().getDeclaringType().getSimpleName());
            log.setType(joinPoint.getSignature().getName());
            log.setContent(java.util.Arrays.toString(joinPoint.getArgs()));
            log.setCreateTime(new Date());

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setIp(request.getRemoteAddr());
                // 可以从 session 中获取当前登录用户
                Object userId = request.getSession().getAttribute("userId");
                Object username = request.getSession().getAttribute("username");
                log.setUserId(userId != null ? userId.toString() : "");
                log.setUsername(username != null ? username.toString() : "未知用户");
            }

            operationLogService.save(log);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
