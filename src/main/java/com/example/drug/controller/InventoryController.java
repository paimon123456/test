package com.example.drug.controller;

import com.example.drug.service.inventory.InventoryService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理接口
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // 分页条件查询库存
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String warehouseId,
                  @RequestParam(required = false) String drugName,
                  @RequestParam(required = false) String status,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return inventoryService.list(warehouseId, drugName, status, pageNum, pageSize);
    }

    // 根据ID查询库存详情
    @GetMapping("/{inventoryId}")
    public Result getById(@PathVariable String inventoryId) {
        return inventoryService.getById(inventoryId);
    }

    // 根据仓库ID查询库存
    @GetMapping("/warehouse/{warehouseId}")
    public Result listByWarehouse(@PathVariable String warehouseId) {
        return inventoryService.listByWarehouse(warehouseId);
    }

    // 查询近效期药品（默认3个月内）
    @GetMapping("/near-expiry")
    public Result listNearExpiry(@RequestParam(required = false, defaultValue = "90") Integer days) {
        return inventoryService.listNearExpiry(days);
    }
}
