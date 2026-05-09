package com.example.drug.service.warehouse;

import com.example.drug.entity.warehouse.WarehouseLocation;
import com.example.drug.util.Result;

public interface WarehouseLocationService {
    // 分页条件查询库位
    Result list(String warehouseId, String status, Integer pageNum, Integer pageSize);
    // 根据ID查询
    Result getById(String locId);
    // 新增库位
    Result add(WarehouseLocation location);
    // 修改库位
    Result update(WarehouseLocation location);
    // 绑定库存
    Result bindInventory(String locId, String status);
}
