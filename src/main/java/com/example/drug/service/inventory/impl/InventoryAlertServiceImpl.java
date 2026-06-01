package com.example.drug.service.inventory.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.inventory.InventoryAlertService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 库存预警服务实现类（实时查询，不存储）
 */
@Service
public class InventoryAlertServiceImpl implements InventoryAlertService {
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    /**
     * 查询低于最低库存的药品列表（实时查询）
     */
    @Override
    public Result listLowStock(Integer pageNum, Integer pageSize) {
        // 查询所有设置了最低库存的药品
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Inventory::getMinStock, 0);  // min_stock > 0
        
        List<Inventory> allInventories = inventoryMapper.selectList(wrapper);
        
        // 过滤出库存数量 < 最低库存阈值的记录
        List<Inventory> lowStockList = allInventories.stream()
                .filter(inv -> inv.getStockNum() != null && inv.getMinStock() != null 
                        && inv.getStockNum() < inv.getMinStock())
                .toList();
        
        // 分页处理
        int total = lowStockList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Inventory> pageRecords = fromIndex < total ? 
                lowStockList.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 获取低库存药品数量
     */
    @Override
    public Result getLowStockCount() {
        // 查询所有设置了最低库存的药品
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Inventory::getMinStock, 0);
        
        List<Inventory> allInventories = inventoryMapper.selectList(wrapper);
        
        // 过滤出库存数量 < 最低库存阈值的记录
        long count = allInventories.stream()
                .filter(inv -> inv.getStockNum() != null && inv.getMinStock() != null 
                        && inv.getStockNum() < inv.getMinStock())
                .count();
        
        return Result.success(count);
    }
}
