package com.example.drug.service.inventory;

import com.example.drug.dto.InventoryCheckDTO;
import com.example.drug.util.Result;

/**
 * 库存盘点服务接口
 */
public interface InventoryCheckService {
    
    /**
     * 创建盘点单
     */
    Result createCheck(InventoryCheckDTO dto);
    
    /**
     * 录入实际库存数量
     */
    Result inputActualStock(String checkId, Integer actualStock);
    
    /**
     * 审核盘点单
     */
    Result auditCheck(String checkId, String auditorId, Boolean approved, String remark);
    
    /**
     * 查询盘点单列表
     */
    Result list(String drugName, String auditStatus, String startDate, String endDate, 
                Integer pageNum, Integer pageSize);
    
    /**
     * 根据盘点单ID查询详情
     */
    Result getById(String checkId);
}
