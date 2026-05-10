package com.example.drug.service.inventory;

import com.example.drug.dto.PurchaseInDTO;
import com.example.drug.util.Result;

/**
 * 药品入库服务接口
 */
public interface PurchaseInService {
    
    /**
     * 药品入库（采购入库）
     */
    Result purchaseIn(PurchaseInDTO dto);
    
    /**
     * 查询入库单列表
     */
    Result list(String orderId, String drugName, Integer pageNum, Integer pageSize);
    
    /**
     * 根据入库单号查询详情
     */
    Result getById(String inId);
}
