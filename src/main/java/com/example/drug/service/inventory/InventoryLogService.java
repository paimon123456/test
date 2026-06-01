package com.example.drug.service.inventory;

import com.example.drug.util.Result;

/**
 * 库存变动日志服务接口（使用sys_log表）
 */
public interface InventoryLogService {
    
    /**
     * 查询库存变动日志列表
     */
    Result list(String drugId, String changeType, String startDate, String endDate, 
                Integer pageNum, Integer pageSize);
    
    /**
     * 记录库存变动日志（写入sys_log表）
     */
    void logInventoryChange(String inventoryId, String drugId, String batchNo,
                           String changeType, Integer changeNum, 
                           Integer beforeStock, Integer afterStock,
                           String relatedOrderId, String operatorId, String remark);
}
