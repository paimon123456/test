package com.example.drug.service.sales;

import com.example.drug.dto.SalesReturnDTO;
import com.example.drug.util.Result;

/**
 * 销售退货服务接口
 */
public interface SalesReturnService {
    
    /**
     * 创建退货申请
     */
    Result createReturn(SalesReturnDTO dto);
    
    /**
     * 审核退货申请
     */
    Result auditReturn(String returnId, String auditorId, Boolean approved, String remark);
    
    /**
     * 查询退货单列表
     */
    Result list(String originalOrderId, String drugName, String status,
                String startDate, String endDate, Integer pageNum, Integer pageSize);
    
    /**
     * 根据退货单ID查询详情
     */
    Result getById(String returnId);
}
