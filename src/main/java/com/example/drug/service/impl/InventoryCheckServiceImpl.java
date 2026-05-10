package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.InventoryCheckDTO;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.entity.inventory.InventoryCheck;
import com.example.drug.mapper.InventoryCheckMapper;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.inventory.InventoryCheckService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 库存盘点服务实现类
 */
@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {
    
    @Autowired
    private InventoryCheckMapper inventoryCheckMapper;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    /**
     * 创建盘点单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createCheck(InventoryCheckDTO dto) {
        try {
            List<Inventory> inventories;
            
            if ("全盘".equals(dto.getCheckType())) {
                // 全盘：查询指定仓库的所有库存
                LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                if (dto.getWarehouseId() != null && !dto.getWarehouseId().isEmpty()) {
                    wrapper.eq(Inventory::getWarehouseId, dto.getWarehouseId());
                }
                inventories = inventoryMapper.selectList(wrapper);
            } else if ("抽盘".equals(dto.getCheckType())) {
                // 抽盘：查询指定药品的库存
                LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(Inventory::getDrugId, dto.getDrugIds());
                inventories = inventoryMapper.selectList(wrapper);
            } else {
                return Result.fail("盘点类型错误，应为全盘或抽盘");
            }
            
            if (inventories.isEmpty()) {
                return Result.fail("未找到需要盘点的库存");
            }
            
            // 为每个库存创建盘点单
            int count = 0;
            Date now = new Date();
            
            for (Inventory inventory : inventories) {
                InventoryCheck check = new InventoryCheck();
                check.setCheckId(UUIDUtil.getUUID());
                check.setDrugId(inventory.getDrugId());
                check.setSystemStock(inventory.getStockNum());
                check.setActualStock(null); // 待录入
                check.setDiffNum(null);
                check.setCheckDate(now);
                check.setCheckerId(dto.getCheckerId());
                check.setAuditStatus("待审核");
                check.setAdjustStatus("未调整");
                check.setCreateTime(now);
                
                inventoryCheckMapper.insert(check);
                count++;
            }
            
            return Result.success("盘点单创建成功，共" + count + "条记录");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建盘点单失败：" + e.getMessage());
        }
    }
    
    /**
     * 录入实际库存数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result inputActualStock(String checkId, Integer actualStock) {
        try {
            InventoryCheck check = inventoryCheckMapper.selectById(checkId);
            if (check == null) {
                return Result.fail("盘点单不存在");
            }
            
            if ("已通过".equals(check.getAuditStatus())) {
                return Result.fail("盘点单已审核，无法修改");
            }
            
            check.setActualStock(actualStock);
            
            // 计算差异
            int diffNum = actualStock - check.getSystemStock();
            check.setDiffNum(diffNum);
            
            inventoryCheckMapper.updateById(check);
            
            return Result.success("录入成功，差异数量：" + diffNum);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("录入失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核盘点单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result auditCheck(String checkId, String auditorId, Boolean approved, String remark) {
        try {
            InventoryCheck check = inventoryCheckMapper.selectById(checkId);
            if (check == null) {
                return Result.fail("盘点单不存在");
            }
            
            if (!"待审核".equals(check.getAuditStatus())) {
                return Result.fail("盘点单状态不是待审核");
            }
            
            if (check.getActualStock() == null) {
                return Result.fail("请先录入实际库存数量");
            }
            
            if (approved) {
                // 审核通过
                check.setAuditStatus("已通过");
                check.setAuditorId(auditorId);
                
                // 如果有差异，调整库存
                if (check.getDiffNum() != null && check.getDiffNum() != 0) {
                    // 查询对应的库存记录
                    LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Inventory::getDrugId, check.getDrugId());
                    // TODO: 需要根据实际情况匹配正确的库存记录（批号等）
                    
                    Inventory inventory = inventoryMapper.selectOne(wrapper);
                    if (inventory != null) {
                        inventory.setStockNum(check.getActualStock());
                        updateInventoryStatus(inventory);
                        inventoryMapper.updateById(inventory);
                        
                        check.setAdjustStatus("已调整");
                    }
                } else {
                    check.setAdjustStatus("无需调整");
                }
                
                inventoryCheckMapper.updateById(check);
                return Result.success("审核通过，库存已调整");
            } else {
                // 审核驳回
                check.setAuditStatus("已驳回");
                check.setAuditorId(auditorId);
                inventoryCheckMapper.updateById(check);
                
                return Result.success("审核驳回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询盘点单列表
     */
    @Override
    public Result list(String drugName, String auditStatus, String startDate, String endDate,
                       Integer pageNum, Integer pageSize) {
        List<InventoryCheck> checks = inventoryCheckMapper.selectCheckList(
                drugName, auditStatus, startDate, endDate);
        
        // 分页处理
        int total = checks.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<InventoryCheck> pageRecords = fromIndex < total ? 
                checks.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 根据盘点单ID查询详情
     */
    @Override
    public Result getById(String checkId) {
        InventoryCheck check = inventoryCheckMapper.selectCheckDetail(checkId);
        if (check == null) {
            return Result.fail("盘点单不存在");
        }
        return Result.success(check);
    }
    
    /**
     * 更新库存状态
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
