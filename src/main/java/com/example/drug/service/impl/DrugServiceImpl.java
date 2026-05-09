package com.example.drug.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.drug.entity.Drug;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.mapper.DrugMapper;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Override
    public boolean save(Drug drug) {
        drug.setStatus(1); // 默认上架
        return super.save(drug);
    }

    @Override
    @Transactional
    public void resetIdSequence() {
        // 获取所有药品并按ID升序排序
        List<Drug> drugs = this.list();
        if (drugs.isEmpty()) {
            return;
        }
        
        // 按ID升序排序
        drugs.sort((d1, d2) -> {
            if (d1.getDrugId() == null || d2.getDrugId() == null) return 0;
            return d1.getDrugId().compareTo(d2.getDrugId());
        });
        
        // 重置数据库自增序列
        try {
            this.baseMapper.resetAutoIncrement();
        } catch (Exception e) {
            // 忽略错误
        }
    }

    @Override
    public List<Drug> getExpiringDrugs(Integer days) {
        // 从库存表查询近效期药品
        List<Inventory> expiringInventory = inventoryMapper.selectNearExpiry(days);
        
        // 收集药品ID
        List<String> drugIds = new ArrayList<>();
        for (Inventory inv : expiringInventory) {
            if (inv.getDrugId() != null) {
                drugIds.add(inv.getDrugId());
            }
        }
        
        if (drugIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询药品信息
        return this.listByIds(drugIds);
    }
}
