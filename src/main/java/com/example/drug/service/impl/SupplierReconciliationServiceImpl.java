package com.example.drug.service.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.supplier.SupplierReconciliation;
import com.example.drug.entity.purchase.PurchaseOrder;
import com.example.drug.mapper.SupplierReconciliationMapper;
import com.example.drug.mapper.PurchaseOrderMapper;
import com.example.drug.service.supplier.SupplierReconciliationService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 供应商对账服务实现类
 */
@Service
public class SupplierReconciliationServiceImpl implements SupplierReconciliationService {
    
    @Autowired
    private SupplierReconciliationMapper reconciliationMapper;
    
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    
    /**
     * 创建对账单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createReconciliation(String supplierId, String cycle, String orderId) {
        try {
            SupplierReconciliation reconciliation = new SupplierReconciliation();
            reconciliation.setBillId(UUIDUtil.getUUID());
            reconciliation.setSupplierId(supplierId);
            reconciliation.setCycle(cycle);
            reconciliation.setOrderId(orderId);
            
            if (orderId != null && !orderId.isEmpty()) {
                PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
                if (order != null) {
                    reconciliation.setTotalAmount(order.getTotalAmount());
                    reconciliation.setPaidAmount(BigDecimal.ZERO);
                    reconciliation.setUnpaidAmount(order.getTotalAmount());
                }
            }
            
            reconciliation.setStatus("未对账");
            reconciliation.setCreateTime(new Date());
            
            reconciliationMapper.insert(reconciliation);
            return Result.success("对账单创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建对账单失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询对账列表
     */
    @Override
    public Result list(String supplierId, String status, String startDate, String endDate,
                       Integer pageNum, Integer pageSize) {
        List<SupplierReconciliation> reconciliations = reconciliationMapper.selectReconciliationList(
                supplierId, status, startDate, endDate);
        
        int total = reconciliations.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SupplierReconciliation> pageRecords = fromIndex < total ? 
                reconciliations.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 查询对账详情
     */
    @Override
    public Result getById(String billId) {
        SupplierReconciliation reconciliation = reconciliationMapper.selectReconciliationDetail(billId);
        if (reconciliation == null) {
            return Result.fail("对账单不存在");
        }
        return Result.success(reconciliation);
    }
    
    /**
     * 确认对账
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result confirmReconciliation(String billId, String operatorId) {
        try {
            SupplierReconciliation reconciliation = reconciliationMapper.selectById(billId);
            if (reconciliation == null) {
                return Result.fail("对账单不存在");
            }
            
            if (!"未对账".equals(reconciliation.getStatus())) {
                return Result.fail("对账单状态不允许确认");
            }
            
            reconciliation.setStatus("已对账");
            reconciliation.setReconcileTime(new Date());
            
            reconciliationMapper.updateById(reconciliation);
            return Result.success("对账确认成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("确认对账失败：" + e.getMessage());
        }
    }
    
    /**
     * 确认付款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result confirmPayment(String billId, String operatorId) {
        try {
            SupplierReconciliation reconciliation = reconciliationMapper.selectById(billId);
            if (reconciliation == null) {
                return Result.fail("对账单不存在");
            }
            
            if (!"已对账".equals(reconciliation.getStatus())) {
                return Result.fail("请先确认对账");
            }
            
            reconciliation.setStatus("已付款");
            reconciliation.setPaidAmount(reconciliation.getTotalAmount());
            reconciliation.setUnpaidAmount(BigDecimal.ZERO);
            reconciliation.setPaidTime(new Date());
            
            reconciliationMapper.updateById(reconciliation);
            return Result.success("付款确认成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("确认付款失败：" + e.getMessage());
        }
    }
    
    /**
     * 供应商对账统计
     */
    @Override
    public Result statistics(String supplierId) {
        List<SupplierReconciliation> reconciliations;
        
        if (supplierId != null && !supplierId.isEmpty()) {
            reconciliations = reconciliationMapper.selectReconciliationList(supplierId, null, null, null);
        } else {
            reconciliations = reconciliationMapper.selectReconciliationList(null, null, null, null);
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalUnpaid = BigDecimal.ZERO;
        int pendingCount = 0;
        int reconciledCount = 0;
        int paidCount = 0;
        
        for (SupplierReconciliation r : reconciliations) {
            if (r.getTotalAmount() != null) {
                totalAmount = totalAmount.add(r.getTotalAmount());
            }
            if (r.getPaidAmount() != null) {
                totalPaid = totalPaid.add(r.getPaidAmount());
            }
            if (r.getUnpaidAmount() != null) {
                totalUnpaid = totalUnpaid.add(r.getUnpaidAmount());
            }
            
            switch (r.getStatus()) {
                case "未对账":
                    pendingCount++;
                    break;
                case "已对账":
                    reconciledCount++;
                    break;
                case "已付款":
                    paidCount++;
                    break;
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("totalAmount", totalAmount);
        data.put("totalPaid", totalPaid);
        data.put("totalUnpaid", totalUnpaid);
        data.put("pendingCount", pendingCount);
        data.put("reconciledCount", reconciledCount);
        data.put("paidCount", paidCount);
        data.put("totalCount", reconciliations.size());
        
        return Result.success(data);
    }
}
