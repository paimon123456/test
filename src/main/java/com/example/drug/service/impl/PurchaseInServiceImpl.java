package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.PurchaseInDTO;
import com.example.drug.entity.Drug;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.mapper.DrugMapper;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.inventory.PurchaseInService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 药品入库服务实现类
 */
@Service
public class PurchaseInServiceImpl implements PurchaseInService {
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private DrugMapper drugMapper;
    
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
                    existingInventory.setStockNum(existingInventory.getStockNum() + item.getPurchaseNum());
                    existingInventory.setProductionDate(item.getProductionDate());
                    existingInventory.setExpiryDate(item.getExpiryDate());
                    existingInventory.setWarehouseId(item.getWarehouseId());
                    existingInventory.setLocation(item.getLocation());
                    
                    // 更新状态
                    updateInventoryStatus(existingInventory);
                    
                    inventoryMapper.updateById(existingInventory);
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
                }
            }
            
            return Result.success("入库成功，共入库" + dto.getItems().size() + "种药品");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("入库失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询入库单列表（这里简化为查询库存变动记录）
     */
    @Override
    public Result list(String orderId, String drugName, Integer pageNum, Integer pageSize) {
        // TODO: 需要创建purchase_in表和对应的Mapper来实现完整的入库单查询
        // 这里暂时返回空列表
        Map<String, Object> data = new HashMap<>();
        data.put("total", 0);
        data.put("records", List.of());
        return Result.success(data);
    }
    
    /**
     * 根据入库单号查询详情
     */
    @Override
    public Result getById(String inId) {
        // TODO: 需要实现完整的入库单查询
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
}
