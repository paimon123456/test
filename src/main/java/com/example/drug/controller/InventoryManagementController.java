package com.example.drug.controller;

import com.example.drug.dto.InventoryCheckDTO;
import com.example.drug.dto.PurchaseInDTO;
import com.example.drug.service.inventory.InventoryCheckService;
import com.example.drug.service.inventory.PurchaseInService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/inventory")
public class InventoryManagementController {
    
    @Autowired
    private PurchaseInService purchaseInService;
    
    @Autowired
    private InventoryCheckService inventoryCheckService;
    
    // ==================== 药品入库管理 ====================
    
    /**
     * 药品入库（采购入库）
     */
    @PostMapping("/purchase-in")
    public Result purchaseIn(@RequestBody PurchaseInDTO dto) {
        return purchaseInService.purchaseIn(dto);
    }
    
    /**
     * 查询入库单列表
     */
    @GetMapping("/purchase-in/list")
    public Result listPurchaseIn(@RequestParam(required = false) String orderId,
                                  @RequestParam(required = false) String drugName,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return purchaseInService.list(orderId, drugName, pageNum, pageSize);
    }
    
    /**
     * 根据入库单号查询详情
     */
    @GetMapping("/purchase-in/{inId}")
    public Result getPurchaseInById(@PathVariable String inId) {
        return purchaseInService.getById(inId);
    }
    
    // ==================== 库存盘点管理 ====================
    
    /**
     * 创建盘点单
     */
    @PostMapping("/check/create")
    public Result createCheck(@RequestBody InventoryCheckDTO dto) {
        return inventoryCheckService.createCheck(dto);
    }
    
    /**
     * 录入实际库存数量
     */
    @PostMapping("/check/input-stock")
    public Result inputActualStock(@RequestParam String checkId,
                                    @RequestParam Integer actualStock) {
        return inventoryCheckService.inputActualStock(checkId, actualStock);
    }
    
    /**
     * 审核盘点单
     */
    @PostMapping("/check/audit")
    public Result auditCheck(@RequestParam String checkId,
                             @RequestParam String auditorId,
                             @RequestParam Boolean approved,
                             @RequestParam(required = false) String remark) {
        return inventoryCheckService.auditCheck(checkId, auditorId, approved, remark);
    }
    
    /**
     * 查询盘点单列表
     */
    @GetMapping("/check/list")
    public Result listCheck(@RequestParam(required = false) String drugName,
                            @RequestParam(required = false) String auditStatus,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return inventoryCheckService.list(drugName, auditStatus, startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 根据盘点单ID查询详情
     */
    @GetMapping("/check/{checkId}")
    public Result getCheckById(@PathVariable String checkId) {
        return inventoryCheckService.getById(checkId);
    }
}
