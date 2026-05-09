package com.example.drug.service.inventory.impl;

import com.example.drug.entity.inventory.Inventory;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.inventory.InventoryService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Result list(String warehouseId, String drugName, String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<Inventory> list = inventoryMapper.selectByCondition(warehouseId, drugName, status);
        PageInfo<Inventory> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result getById(String inventoryId) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        return Result.success(inventory);
    }

    @Override
    public Result listByWarehouse(String warehouseId) {
        List<Inventory> list = inventoryMapper.selectByWarehouseId(warehouseId);
        return Result.success(list);
    }

    @Override
    public Result listNearExpiry(Integer days) {
        if (days == null) {
            days = 90; // 默认3个月
        }
        List<Inventory> list = inventoryMapper.selectNearExpiry(days);
        return Result.success(list);
    }

    /**
     * 定时任务：每日凌晨1点自动锁定过期药品
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoLockExpiredDrugs() {
        System.out.println("========== 开始执行过期药品自动锁定任务 ==========");
        int count = inventoryMapper.lockExpiredDrugs();
        System.out.println("========== 过期药品自动锁定任务完成，共锁定 " + count + " 条记录 ==========");
    }
}
