package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.PurchaseItemDTO;
import com.example.drug.dto.PurchaseOrderDTO;
import com.example.drug.entity.Drug;
import com.example.drug.entity.purchase.InventoryReserve;
import com.example.drug.entity.purchase.PurchaseItem;
import com.example.drug.entity.purchase.PurchaseOrder;
import com.example.drug.mapper.*;
import com.example.drug.service.purchase.PurchaseOrderService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 采购订单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    
    @Autowired
    private PurchaseItemMapper purchaseItemMapper;
    
    @Autowired
    private InventoryReserveMapper inventoryReserveMapper;
    
    @Autowired
    private DrugMapper drugMapper;
    
    /**
     * 创建采购单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createPurchaseOrder(PurchaseOrderDTO dto) {
        try {
            if (dto.getItems() == null || dto.getItems().isEmpty()) {
                return Result.fail("请添加采购明细");
            }
            
            // 生成采购单号
            String orderNo = generateOrderNo();
            String orderId = UUIDUtil.getUUID();
            
            // 计算总金额和总数量
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalQuantity = 0;
            
            for (PurchaseItemDTO itemDto : dto.getItems()) {
                Drug drug = null;
                // 如果传了 drugId，直接用 drugId 查询
                if (itemDto.getDrugId() != null && !itemDto.getDrugId().isEmpty()) {
                    drug = drugMapper.selectById(itemDto.getDrugId());
                }
                // 如果没找到或没有 drugId，尝试根据药品名称查询
                if (drug == null && itemDto.getDrugName() != null && !itemDto.getDrugName().isEmpty()) {
                    LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
                    wrapper.like(Drug::getDrugName, itemDto.getDrugName());
                    drug = drugMapper.selectOne(wrapper);
                }
                if (drug == null) {
                    return Result.fail("药品不存在: " + (itemDto.getDrugName() != null ? itemDto.getDrugName() : itemDto.getDrugId()));
                }
                // 设置 drugId（如果之前为空）
                if (itemDto.getDrugId() == null || itemDto.getDrugId().isEmpty()) {
                    itemDto.setDrugId(drug.getDrugId());
                }
                
                BigDecimal subtotal = itemDto.getPurchasePrice().multiply(new BigDecimal(itemDto.getPurchaseNum()));
                totalAmount = totalAmount.add(subtotal);
                totalQuantity += itemDto.getPurchaseNum();
            }
            
            // 创建采购单
            PurchaseOrder order = new PurchaseOrder();
            order.setOrderId(orderId);
            order.setOrderNo(orderNo);
            order.setSupplierId(dto.getSupplierId());
            order.setWarehouseId(dto.getWarehouseId());
            order.setOperatorId(dto.getOperatorId());
            order.setTotalQuantity(totalQuantity);
            order.setTotalAmount(totalAmount);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setPaidAmount(BigDecimal.ZERO);
            order.setStatus("待审核");
            order.setExpectedDate(dto.getExpectedDate());
            order.setRemark(dto.getRemark());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            
            purchaseOrderMapper.insert(order);
            
            // 创建采购明细
            List<PurchaseItem> items = new ArrayList<>();
            for (PurchaseItemDTO itemDto : dto.getItems()) {
                PurchaseItem item = new PurchaseItem();
                item.setItemId(UUIDUtil.getUUID());
                item.setOrderId(orderId);
                item.setDrugId(itemDto.getDrugId());
                item.setPurchaseNum(itemDto.getPurchaseNum());
                item.setPurchasePrice(itemDto.getPurchasePrice());
                item.setSubtotal(itemDto.getPurchasePrice().multiply(new BigDecimal(itemDto.getPurchaseNum())));
                item.setReceivedNum(0);
                item.setPendingNum(itemDto.getPurchaseNum());
                item.setBatchNo(itemDto.getBatchNo());
                item.setProductionDate(itemDto.getProductionDate());
                item.setExpiryDate(itemDto.getExpiryDate());
                item.setStatus("待入库");
                items.add(item);
                
                // 创建库存预占记录
                InventoryReserve reserve = new InventoryReserve();
                reserve.setReserveId(UUIDUtil.getUUID());
                reserve.setOrderId(orderId);
                reserve.setOrderType("采购");
                reserve.setDrugId(itemDto.getDrugId());
                reserve.setReserveNum(itemDto.getPurchaseNum());
                reserve.setStatus("预占中");
                // 预占有效期7天
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 7);
                reserve.setExpireTime(cal.getTime());
                reserve.setOperatorId(dto.getOperatorId());
                reserve.setCreateTime(new Date());
                inventoryReserveMapper.insert(reserve);
            }
            
            purchaseItemMapper.batchInsert(items);
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("orderNo", orderNo);
            data.put("totalAmount", totalAmount);
            data.put("totalQuantity", totalQuantity);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建采购单失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核采购单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result auditPurchaseOrder(String orderId, String auditorId, Boolean approved, String remark) {
        try {
            PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
            if (order == null) {
                return Result.fail("采购单不存在");
            }
            
            if (!"待审核".equals(order.getStatus())) {
                return Result.fail("采购单状态不是待审核");
            }
            
            if (approved) {
                order.setStatus("已通过");
                order.setAuditorId(auditorId);
                order.setAuditTime(new Date());
                if (remark != null) {
                    order.setRemark(order.getRemark() + " [审核通过: " + remark + "]");
                }
            } else {
                order.setStatus("已驳回");
                order.setAuditorId(auditorId);
                order.setAuditTime(new Date());
                if (remark != null) {
                    order.setRemark(order.getRemark() + " [审核驳回: " + remark + "]");
                }
                
                // 驳回时释放库存预占
                releaseReserves(orderId);
            }
            
            order.setUpdateTime(new Date());
            purchaseOrderMapper.updateById(order);
            
            return Result.success(approved ? "审核通过" : "审核驳回");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("审核采购单失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消采购单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelPurchaseOrder(String orderId, String operatorId) {
        try {
            PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
            if (order == null) {
                return Result.fail("采购单不存在");
            }
            
            if (!"待审核".equals(order.getStatus()) && !"已通过".equals(order.getStatus())) {
                return Result.fail("当前状态不允许取消");
            }
            
            order.setStatus("已取消");
            order.setUpdateTime(new Date());
            purchaseOrderMapper.updateById(order);
            
            // 释放库存预占
            releaseReserves(orderId);
            
            return Result.success("采购单取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("取消采购单失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询采购单列表
     */
    @Override
    public Result list(String orderNo, String supplierId, String status, String startDate, 
                       String endDate, Integer pageNum, Integer pageSize) {
        List<PurchaseOrder> orders = purchaseOrderMapper.selectPurchaseOrderList(
                orderNo, supplierId, status, startDate, endDate);
        
        // 分页处理
        int total = orders.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<PurchaseOrder> pageRecords = fromIndex < total ? 
                orders.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 查询采购单详情
     */
    @Override
    public Result getById(String orderId) {
        PurchaseOrder order = purchaseOrderMapper.selectPurchaseOrderDetail(orderId);
        if (order == null) {
            return Result.fail("采购单不存在");
        }
        
        // 查询明细
        List<PurchaseItem> items = purchaseOrderMapper.selectPurchaseItems(orderId);
        order.setItems(items);
        
        return Result.success(order);
    }
    
    /**
     * 统计待审核采购单
     */
    @Override
    public Result countPendingOrders() {
        Integer count = purchaseOrderMapper.countPendingOrders();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }
    
    /**
     * 统计采购金额
     */
    @Override
    public Result statistics(String startDate, String endDate) {
        BigDecimal amount = purchaseOrderMapper.sumPurchaseAmount(startDate, endDate);
        Map<String, Object> data = new HashMap<>();
        data.put("totalAmount", amount != null ? amount : BigDecimal.ZERO);
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        return Result.success(data);
    }
    
    /**
     * 根据供应商查询采购历史
     */
    @Override
    public Result getPurchaseHistoryBySupplier(String supplierId, Integer pageNum, Integer pageSize) {
        List<PurchaseOrder> orders = purchaseOrderMapper.selectPurchaseOrderList(
                null, supplierId, null, null, null);
        
        int total = orders.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<PurchaseOrder> pageRecords = fromIndex < total ? 
                orders.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 生成采购单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        
        // 查询当天最大序号
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(PurchaseOrder::getOrderNo, "PO" + dateStr)
               .orderByDesc(PurchaseOrder::getCreateTime)
               .last("LIMIT 1");
        PurchaseOrder lastOrder = purchaseOrderMapper.selectOne(wrapper);
        
        int seq = 1;
        if (lastOrder != null) {
            String lastNo = lastOrder.getOrderNo();
            String lastSeq = lastNo.substring(lastNo.length() - 3);
            try {
                seq = Integer.parseInt(lastSeq) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        
        return String.format("PO%s%03d", dateStr, seq);
    }
    
    /**
     * 释放库存预占
     */
    private void releaseReserves(String orderId) {
        List<InventoryReserve> reserves = inventoryReserveMapper.selectByOrderId(orderId);
        for (InventoryReserve reserve : reserves) {
            if ("预占中".equals(reserve.getStatus())) {
                reserve.setStatus("已释放");
                inventoryReserveMapper.updateById(reserve);
            }
        }
    }
}
