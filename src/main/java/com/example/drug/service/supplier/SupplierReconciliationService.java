package com.example.drug.service.supplier;

import com.example.drug.util.Result;
import java.util.Map;

/**
 * 供应商对账服务接口
 */
public interface SupplierReconciliationService {
    
    /**
     * 创建对账单
     */
    Result createReconciliation(String supplierId, String cycle, String orderId);
    
    /**
     * 查询对账列表
     */
    Result list(String supplierId, String status, String startDate, String endDate,
                Integer pageNum, Integer pageSize);
    
    /**
     * 查询对账详情
     */
    Result getById(String billId);
    
    /**
     * 确认对账
     */
    Result confirmReconciliation(String billId, String operatorId);
    
    /**
     * 确认付款
     */
    Result confirmPayment(String billId, String operatorId);
    
    /**
     * 供应商对账统计
     */
    Result statistics(String supplierId);
}
