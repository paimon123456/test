package com.example.drug.controller;

import com.example.drug.service.inventory.InventoryAlertService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存预警控制器（实时查询）
 */
@RestController
@RequestMapping("/inventory/alert")
public class InventoryAlertController {
    
    @Autowired
    private InventoryAlertService inventoryAlertService;
    
    /**
     * 查询低于最低库存的药品列表
     */
    @GetMapping("/low-stock")
    public Result listLowStock(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return inventoryAlertService.listLowStock(pageNum, pageSize);
    }
    
    /**
     * 获取低库存药品数量
     */
    @GetMapping("/low-stock/count")
    public Result getLowStockCount() {
        return inventoryAlertService.getLowStockCount();
    }
}
