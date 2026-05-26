package com.example.drug.task;

import com.example.drug.service.ExceptionLogService;
import com.example.drug.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogCleanupTask {

    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private ExceptionLogService exceptionLogService;
    
    // 默认保留6个月，可通过配置修改
    @Value("${log.retain.months:6}")
    private int retainMonths;

    /**
     * 每天凌晨3点执行自动清理
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoCleanup() {
        System.out.println("===== 日志自动清理任务开始 =====");
        try {
            int opCount = operationLogService.autoCleanLogs(retainMonths);
            System.out.println("清理操作日志: " + opCount + " 条");
            
            int exCount = cleanExceptionLogs();
            System.out.println("清理异常日志: " + exCount + " 条");
        } catch (Exception e) {
            System.err.println("日志自动清理异常: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("===== 日志自动清理任务结束 =====");
    }
    
    private int cleanExceptionLogs() {
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.drug.entity.ExceptionLog> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -retainMonths);
            String cutoffDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            wrapper.lt(com.example.drug.entity.ExceptionLog::getCreateTime, cutoffDate + " 00:00:00");
            
            return exceptionLogService.getBaseMapper().delete(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
