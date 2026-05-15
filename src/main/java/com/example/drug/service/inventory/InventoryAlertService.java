package com.example.drug.service.inventory;

import com.example.drug.util.Result;

/**
 * 库存预警服务接口（实时查询，不存储）
 */
public interface InventoryAlertService {
    
    /**
     * 查询低于最低库存的药品列表（实时查询）
     */
    Result listLowStock(Integer pageNum, Integer pageSize);
    
    /**
     * 获取低库存药品数量
     */
    Result getLowStockCount();
}
