package com.example.drug.service.purchase;

import com.example.drug.dto.PurchaseOrderDTO;
import com.example.drug.util.Result;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService {
    
    /**
     * 创建采购单
     */
    Result createPurchaseOrder(PurchaseOrderDTO dto);
    
    /**
     * 审核采购单
     */
    Result auditPurchaseOrder(String orderId, String auditorId, Boolean approved, String remark);
    
    /**
     * 取消采购单
     */
    Result cancelPurchaseOrder(String orderId, String operatorId);
    
    /**
     * 查询采购单列表
     */
    Result list(String orderNo, String supplierId, String status, String startDate, 
                String endDate, Integer pageNum, Integer pageSize);
    
    /**
     * 查询采购单详情
     */
    Result getById(String orderId);
    
    /**
     * 统计待审核采购单
     */
    Result countPendingOrders();
    
    /**
     * 统计采购金额
     */
    Result statistics(String startDate, String endDate);
    
    /**
     * 根据供应商查询采购历史
     */
    Result getPurchaseHistoryBySupplier(String supplierId, Integer pageNum, Integer pageSize);
}
