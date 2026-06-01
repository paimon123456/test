package com.example.drug.controller;

import com.example.drug.service.inventory.InventoryLogService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存变动日志控制器（使用sys_log表）
 */
@RestController
@RequestMapping("/inventory/log")
public class InventoryLogController {
    
    @Autowired
    private InventoryLogService inventoryLogService;
    
    /**
     * 查询库存变动日志列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String drugId,
                       @RequestParam(required = false) String changeType,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return inventoryLogService.list(drugId, changeType, startDate, endDate, pageNum, pageSize);
    }
}
