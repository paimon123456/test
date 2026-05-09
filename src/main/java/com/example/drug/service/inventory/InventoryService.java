package com.example.drug.service.inventory;

import com.example.drug.util.Result;

/**
 * 库存服务接口
 */
public interface InventoryService {

    // 分页条件查询库存
    Result list(String warehouseId, String drugName, String status, Integer pageNum, Integer pageSize);

    // 根据ID查询库存详情
    Result getById(String inventoryId);

    // 根据仓库ID查询库存
    Result listByWarehouse(String warehouseId);

    // 查询近效期药品
    Result listNearExpiry(Integer days);
}
