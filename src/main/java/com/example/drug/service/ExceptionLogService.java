package com.example.drug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.drug.entity.ExceptionLog;
import com.example.drug.util.Result;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ExceptionLogService extends IService<ExceptionLog> {
    
    /**
     * 分页查询异常日志
     */
    Result queryLogs(Map<String, Object> params);
    
    /**
     * 导出异常日志Excel
     */
    void exportExcel(Map<String, Object> params, HttpServletResponse response);
    
    /**
     * 清理异常日志
     */
    Result cleanLogs(String startDate, String endDate, String operator);
    
    /**
     * 获取异常详情
     */
    Result getDetail(String exceptionId);
}
