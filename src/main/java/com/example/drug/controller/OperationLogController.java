package com.example.drug.controller;

import com.example.drug.service.ExceptionLogService;
import com.example.drug.service.OperationLogService;
import com.example.drug.util.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private ExceptionLogService exceptionLogService;

    /**
     * 查询操作日志（分页 + 多条件筛选）
     */
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        return operationLogService.queryLogs(params);
    }

    /**
     * 导出操作日志Excel
     */
    @GetMapping("/export")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        operationLogService.exportExcel(params, response);
    }

    /**
     * 清理操作日志
     */
    @PostMapping("/clean")
    public Result clean(@RequestParam String startDate,
                        @RequestParam String endDate,
                        @RequestParam(defaultValue = "admin") String operator) {
        return operationLogService.cleanLogs(startDate, endDate, operator);
    }

    // ============ 异常日志接口 ============

    /**
     * 查询异常日志（分页 + 多条件筛选）
     */
    @GetMapping("/exception/list")
    public Result exceptionList(@RequestParam Map<String, Object> params) {
        return exceptionLogService.queryLogs(params);
    }

    /**
     * 获取异常详情（含堆栈信息）
     */
    @GetMapping("/exception/detail")
    public Result exceptionDetail(@RequestParam String exceptionId) {
        return exceptionLogService.getDetail(exceptionId);
    }

    /**
     * 导出异常日志Excel
     */
    @GetMapping("/exception/export")
    public void exceptionExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        exceptionLogService.exportExcel(params, response);
    }

    /**
     * 清理异常日志
     */
    @PostMapping("/exception/clean")
    public Result exceptionClean(@RequestParam String startDate,
                                 @RequestParam String endDate,
                                 @RequestParam(defaultValue = "admin") String operator) {
        return exceptionLogService.cleanLogs(startDate, endDate, operator);
    }
}
