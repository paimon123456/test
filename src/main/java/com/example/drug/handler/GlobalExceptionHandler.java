package com.example.drug.handler;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.ExceptionLog;
import com.example.drug.service.ExceptionLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ExceptionLogService exceptionLogService;

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e, HttpServletRequest request) {
        // 记录异常日志
        try {
            ExceptionLog exceptionLog = new ExceptionLog();
            exceptionLog.setExceptionId(UUIDUtil.getUUID());
            exceptionLog.setExceptionType(e.getClass().getName());
            exceptionLog.setErrorMessage(e.getMessage() != null ? e.getMessage() : e.toString());

            // 获取完整堆栈
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            exceptionLog.setStackTrace(stackTrace.length() > 10000 ? stackTrace.substring(0, 10000) : stackTrace);

            exceptionLog.setRequestUrl(request.getRequestURI());
            exceptionLog.setRequestMethod(request.getMethod());

            Object userId = request.getSession().getAttribute("userId");
            Object username = request.getSession().getAttribute("username");
            exceptionLog.setUserId(userId != null ? userId.toString() : "");
            exceptionLog.setUsername(username != null ? username.toString() : "未知用户");
            exceptionLog.setIp(request.getRemoteAddr());
            exceptionLog.setCreateTime(new Date());

            exceptionLogService.save(exceptionLog);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 返回友好的错误信息
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "系统异常，请联系管理员");
        result.put("data", null);
        return result;
    }
}
