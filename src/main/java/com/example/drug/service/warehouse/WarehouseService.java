package com.example.drug.service.warehouse;

import com.example.drug.entity.warehouse.Warehouse;
import com.example.drug.util.Result;

public interface WarehouseService {
    // 分页查询仓库列表（支持按名称和状态查询）
    Result list(String warehouseName, Integer status, Integer pageNum, Integer pageSize);
    // 根据ID查询
    Result getById(String warehouseId);
    // 新增仓库
    Result add(Warehouse warehouse);
    // 修改仓库
    Result update(Warehouse warehouse);
    // 删除仓库
    Result delete(String warehouseId);
}
