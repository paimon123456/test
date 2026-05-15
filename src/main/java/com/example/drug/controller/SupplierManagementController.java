package com.example.drug.controller;

import com.example.drug.dto.CooperationRecordDTO;
import com.example.drug.dto.SupplierDTO;
import com.example.drug.service.supplier.SupplierCooperationService;
import com.example.drug.service.supplier.SupplierReconciliationService;
import com.example.drug.service.supplier.SupplierService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商管理控制器
 */
@RestController
@RequestMapping("/supplier")
public class SupplierManagementController {
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private SupplierCooperationService cooperationService;
    
    @Autowired
    private SupplierReconciliationService reconciliationService;
    
    // ==================== 供应商档案管理 ====================
    
    /**
     * 新增供应商
     */
    @PostMapping("/add")
    public Result addSupplier(@RequestBody SupplierDTO dto) {
        return supplierService.addSupplier(dto);
    }
    
    /**
     * 修改供应商
     */
    @PostMapping("/update")
    public Result updateSupplier(@RequestBody SupplierDTO dto) {
        return supplierService.updateSupplier(dto);
    }
    
    /**
     * 删除供应商
     */
    @PostMapping("/delete")
    public Result deleteSupplier(@RequestParam String supplierId) {
        return supplierService.deleteSupplier(supplierId);
    }
    
    /**
     * 查询供应商列表
     */
    @GetMapping("/list")
    public Result listSupplier(@RequestParam(required = false) String supplierName,
                               @RequestParam(required = false) String qualificationStatus,
                               @RequestParam(required = false) String cooperationStatus,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return supplierService.list(supplierName, qualificationStatus, cooperationStatus, status, pageNum, pageSize);
    }
    
    /**
     * 查询供应商详情
     */
    @GetMapping("/{supplierId}")
    public Result getSupplierById(@PathVariable String supplierId) {
        return supplierService.getById(supplierId);
    }
    
    /**
     * 根据编码查询供应商
     */
    @GetMapping("/code/{supplierCode}")
    public Result getSupplierByCode(@PathVariable String supplierCode) {
        return supplierService.getByCode(supplierCode);
    }
    
    /**
     * 资质过期提醒
     */
    @GetMapping("/qualification/reminder")
    public Result qualificationReminder(@RequestParam(required = false, defaultValue = "30") Integer days) {
        return supplierService.qualificationReminder(days);
    }
    
    // ==================== 合作记录管理 ====================
    
    /**
     * 添加合作记录
     */
    @PostMapping("/cooperation/add")
    public Result addCooperationRecord(@RequestBody CooperationRecordDTO dto) {
        return cooperationService.addCooperationRecord(dto);
    }
    
    /**
     * 查询合作记录列表
     */
    @GetMapping("/cooperation/list")
    public Result listCooperation(@RequestParam(required = false) String supplierId,
                                  @RequestParam(required = false) String recordType,
                                  @RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return cooperationService.list(supplierId, recordType, startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 查询合作记录详情
     */
    @GetMapping("/cooperation/{recordId}")
    public Result getCooperationById(@PathVariable String recordId) {
        return cooperationService.getById(recordId);
    }
    
    /**
     * 删除合作记录
     */
    @PostMapping("/cooperation/delete")
    public Result deleteCooperation(@RequestParam String recordId) {
        return cooperationService.delete(recordId);
    }
    
    // ==================== 对账管理 ====================
    
    /**
     * 创建对账单
     */
    @PostMapping("/reconciliation/create")
    public Result createReconciliation(@RequestParam String supplierId,
                                      @RequestParam String cycle,
                                      @RequestParam(required = false) String orderId) {
        return reconciliationService.createReconciliation(supplierId, cycle, orderId);
    }
    
    /**
     * 查询对账列表
     */
    @GetMapping("/reconciliation/list")
    public Result listReconciliation(@RequestParam(required = false) String supplierId,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return reconciliationService.list(supplierId, status, startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 查询对账详情
     */
    @GetMapping("/reconciliation/{billId}")
    public Result getReconciliationById(@PathVariable String billId) {
        return reconciliationService.getById(billId);
    }
    
    /**
     * 确认对账
     */
    @PostMapping("/reconciliation/confirm")
    public Result confirmReconciliation(@RequestParam String billId, @RequestParam String operatorId) {
        return reconciliationService.confirmReconciliation(billId, operatorId);
    }
    
    /**
     * 确认付款
     */
    @PostMapping("/reconciliation/payment")
    public Result confirmPayment(@RequestParam String billId, @RequestParam String operatorId) {
        return reconciliationService.confirmPayment(billId, operatorId);
    }
    
    /**
     * 对账统计
     */
    @GetMapping("/reconciliation/statistics")
    public Result reconciliationStatistics(@RequestParam(required = false) String supplierId) {
        return reconciliationService.statistics(supplierId);
    }
}
