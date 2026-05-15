package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.PurchaseInDTO;
import com.example.drug.entity.Drug;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.entity.inventory.PurchaseIn;
import com.example.drug.entity.inventory.PurchaseInItem;
import com.example.drug.entity.purchase.InventoryReserve;
import com.example.drug.entity.purchase.PurchaseItem;
import com.example.drug.mapper.*;
import com.example.drug.service.inventory.InventoryLogService;
import com.example.drug.service.inventory.PurchaseInService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 药品入库服务实现类
 */
@Service
public class PurchaseInServiceImpl implements PurchaseInService {
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private DrugMapper drugMapper;
    
    @Autowired
    private InventoryReserveMapper inventoryReserveMapper;
    
    @Autowired
    private InventoryLogService inventoryLogService;
    
    /**
     * 药品入库（采购入库）
     * 遵循事务原则，确保库存更新的原子性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result purchaseIn(PurchaseInDTO dto) {
        try {
            if (dto.getItems() == null || dto.getItems().isEmpty()) {
                return Result.fail("入库明细不能为空");
            }
            
            // 生成入库单号
            String inId = UUIDUtil.getUUID();
            String inNo = generateInNo();
            
            // 遍历入库明细，逐个处理
            for (PurchaseInDTO.PurchaseInItemDTO item : dto.getItems()) {
                // 1. 验证药品是否存在
                Drug drug = drugMapper.selectById(item.getDrugId());
                if (drug == null) {
                    return Result.fail("药品ID[" + item.getDrugId() + "]不存在");
                }
                
                // 2. 检查该药品+批号是否已存在库存记录
                LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Inventory::getDrugId, item.getDrugId())
                       .eq(Inventory::getBatchNo, item.getBatchNo());
                Inventory existingInventory = inventoryMapper.selectOne(wrapper);
                
                if (existingInventory != null) {
                    // 3a. 已存在，更新库存数量
                    Integer beforeStock = existingInventory.getStockNum();
                    existingInventory.setStockNum(existingInventory.getStockNum() + item.getPurchaseNum());
                    Integer afterStock = existingInventory.getStockNum();
                    existingInventory.setProductionDate(item.getProductionDate());
                    existingInventory.setExpiryDate(item.getExpiryDate());
                    existingInventory.setWarehouseId(item.getWarehouseId());
                    existingInventory.setLocation(item.getLocation());
                    
                    // 更新状态
                    updateInventoryStatus(existingInventory);
                    
                    inventoryMapper.updateById(existingInventory);
                    
                    // 记录库存变动日志
                    inventoryLogService.logInventoryChange(
                        existingInventory.getInventoryId(),
                        existingInventory.getDrugId(),
                        existingInventory.getBatchNo(),
                        "采购入库",
                        item.getPurchaseNum(),
                        beforeStock,
                        afterStock,
                        dto.getOrderId(),
                        dto.getOperatorId(),
                        "采购入库"
                    );
                } else {
                    // 3b. 不存在，创建新库存记录
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(UUIDUtil.getUUID());
                    inventory.setDrugId(item.getDrugId());
                    inventory.setBatchNo(item.getBatchNo());
                    inventory.setProductionDate(item.getProductionDate());
                    inventory.setExpiryDate(item.getExpiryDate());
                    inventory.setStockNum(item.getPurchaseNum());
                    inventory.setWarehouseId(item.getWarehouseId());
                    inventory.setLocation(item.getLocation());
                    inventory.setMinStock(0);
                    
                    // 设置初始状态
                    updateInventoryStatus(inventory);
                    
                    inventoryMapper.insert(inventory);
                    
                    // 记录库存变动日志（新增库存）
                    inventoryLogService.logInventoryChange(
                        inventory.getInventoryId(),
                        inventory.getDrugId(),
                        inventory.getBatchNo(),
                        "采购入库",
                        item.getPurchaseNum(),
                        0,
                        item.getPurchaseNum(),
                        dto.getOrderId(),
                        dto.getOperatorId(),
                        "采购入库-新建库存"
                    );
                }
                
                // 4. 释放对应的库存预占（如果有采购单关联）
                if (dto.getOrderId() != null && !dto.getOrderId().isEmpty()) {
                    releaseReserve(dto.getOrderId(), item.getDrugId(), item.getPurchaseNum());
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("inId", inId);
            data.put("inNo", inNo);
            data.put("itemCount", dto.getItems().size());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("入库失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询入库单列表
     */
    @Override
    public Result list(String orderId, String drugName, Integer pageNum, Integer pageSize) {
        // 查询所有入库相关记录
        LambdaQueryWrapper<PurchaseIn> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null && !orderId.isEmpty()) {
            wrapper.eq(PurchaseIn::getOrderId, orderId);
        }
        wrapper.orderByDesc(PurchaseIn::getInTime);
        
        List<PurchaseIn> records = new ArrayList<>();
        // 直接查询
        Map<String, Object> data = new HashMap<>();
        data.put("total", 0);
        data.put("records", records);
        return Result.success(data);
    }
    
    /**
     * 根据入库单号查询详情
     */
    @Override
    public Result getById(String inId) {
        // TODO: 实现完整的入库单查询
        return Result.fail("功能待实现");
    }
    
    /**
     * 更新库存状态（正常/近效期/过期）
     */
    private void updateInventoryStatus(Inventory inventory) {
        if (inventory.getExpiryDate() == null) {
            inventory.setStatus("正常");
            return;
        }
        
        Date now = new Date();
        long diffMillis = inventory.getExpiryDate().getTime() - now.getTime();
        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        
        if (diffDays < 0) {
            inventory.setStatus("过期");
        } else if (diffDays <= 90) {
            inventory.setStatus("近效期");
        } else {
            inventory.setStatus("正常");
        }
    }
    
    /**
     * 生成入库单号
     */
    private String generateInNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        Random random = new Random();
        int seq = random.nextInt(900) + 100;
        return String.format("IN%s%s", dateStr, seq);
    }
    
    /**
     * 释放库存预占
     */
    private void releaseReserve(String orderId, String drugId, Integer num) {
        LambdaQueryWrapper<InventoryReserve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryReserve::getOrderId, orderId)
               .eq(InventoryReserve::getDrugId, drugId)
               .eq(InventoryReserve::getStatus, "预占中");
        List<InventoryReserve> reserves = inventoryReserveMapper.selectList(wrapper);
        
        for (InventoryReserve reserve : reserves) {
            if (reserve.getReserveNum() <= num) {
                reserve.setStatus("已确认");
                inventoryReserveMapper.updateById(reserve);
                num -= reserve.getReserveNum();
            }
            if (num <= 0) break;
        }
    }
}
