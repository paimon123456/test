package com.example.drug.controller;

import com.example.drug.dto.PurchaseOrderDTO;
import com.example.drug.service.purchase.PurchaseOrderService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 采购管理控制器
 */
@RestController
@RequestMapping("/purchase")
public class PurchaseManagementController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    // ==================== 采购单管理 ====================
    
    /**
     * 创建采购单
     */
    @PostMapping("/order/create")
    public Result createPurchaseOrder(@RequestBody PurchaseOrderDTO dto) {
        return purchaseOrderService.createPurchaseOrder(dto);
    }
    
    /**
     * 审核采购单
     */
    @PostMapping("/order/audit")
    public Result auditPurchaseOrder(@RequestParam String orderId,
                                     @RequestParam String auditorId,
                                     @RequestParam Boolean approved,
                                     @RequestParam(required = false) String remark) {
        return purchaseOrderService.auditPurchaseOrder(orderId, auditorId, approved, remark);
    }
    
    /**
     * 取消采购单
     */
    @PostMapping("/order/cancel")
    public Result cancelPurchaseOrder(@RequestParam String orderId, @RequestParam String operatorId) {
        return purchaseOrderService.cancelPurchaseOrder(orderId, operatorId);
    }
    
    /**
     * 查询采购单列表
     */
    @GetMapping("/order/list")
    public Result listPurchaseOrder(@RequestParam(required = false) String orderNo,
                                   @RequestParam(required = false) String supplierId,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return purchaseOrderService.list(orderNo, supplierId, status, startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 查询采购单详情
     */
    @GetMapping("/order/{orderId}")
    public Result getPurchaseOrderById(@PathVariable String orderId) {
        return purchaseOrderService.getById(orderId);
    }
    
    /**
     * 统计待审核采购单
     */
    @GetMapping("/order/pending/count")
    public Result countPendingOrders() {
        return purchaseOrderService.countPendingOrders();
    }
    
    /**
     * 采购金额统计
     */
    @GetMapping("/order/statistics")
    public Result statistics(@RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate) {
        return purchaseOrderService.statistics(startDate, endDate);
    }
    
    /**
     * 根据供应商查询采购历史
     */
    @GetMapping("/order/supplier/history")
    public Result getPurchaseHistoryBySupplier(@RequestParam String supplierId,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return purchaseOrderService.getPurchaseHistoryBySupplier(supplierId, pageNum, pageSize);
    }
}
