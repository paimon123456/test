package com.example.drug.service.inventory.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.SysLog;
import com.example.drug.mapper.SysLogMapper;
import com.example.drug.service.inventory.InventoryLogService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 库存变动日志服务实现类（使用sys_log表）
 */
@Service
public class InventoryLogServiceImpl implements InventoryLogService {
    
    @Autowired
    private SysLogMapper sysLogMapper;
    
    /**
     * 查询库存变动日志列表
     */
    @Override
    public Result list(String drugId, String changeType, String startDate, String endDate, 
                       Integer pageNum, Integer pageSize) {
        // 从sys_log表查询库存相关的日志
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysLog::getModule, "库存管理");
        
        if (changeType != null && !changeType.isEmpty()) {
            wrapper.eq(SysLog::getType, changeType);
        }
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(SysLog::getCreateTime, startDate);
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(SysLog::getCreateTime, endDate);
        }
        
        wrapper.orderByDesc(SysLog::getCreateTime);
        
        List<SysLog> logs = sysLogMapper.selectList(wrapper);
        
        // 如果指定了drugId，需要过滤content中包含该drugId的日志
        if (drugId != null && !drugId.isEmpty()) {
            logs.removeIf(log -> !log.getContent().contains(drugId));
        }
        
        // 分页处理
        int total = logs.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SysLog> pageRecords = fromIndex < total ? 
                logs.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 记录库存变动日志（写入sys_log表）
     */
    @Override
    public void logInventoryChange(String inventoryId, String drugId, String batchNo,
                                   String changeType, Integer changeNum, 
                                   Integer beforeStock, Integer afterStock,
                                   String relatedOrderId, String operatorId, String remark) {
        try {
            // 构建日志内容（JSON格式字符串）
            String content = String.format(
                "{\"inventoryId\":\"%s\",\"drugId\":\"%s\",\"batchNo\":\"%s\",\"changeType\":\"%s\",\"changeNum\":%d,\"beforeStock\":%d,\"afterStock\":%d,\"relatedOrderId\":\"%s\",\"remark\":\"%s\"}",
                inventoryId, drugId, batchNo, changeType, changeNum, beforeStock, afterStock, 
                relatedOrderId != null ? relatedOrderId : "", 
                remark != null ? remark.replace("\"", "'") : ""
            );
            
            // 创建日志记录
            SysLog log = new SysLog();
            log.setLogId(UUIDUtil.getUUID());
            log.setModule("库存管理");
            log.setType(changeType);  // 采购入库/销售出库等
            log.setUserId(operatorId);
            log.setUsername("");  // 可以从用户表查询
            log.setContent(content);
            log.setIp("");
            log.setCreateTime(new Date());
            
            sysLogMapper.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("记录库存变动日志失败: " + e.getMessage());
        }
    }
}
