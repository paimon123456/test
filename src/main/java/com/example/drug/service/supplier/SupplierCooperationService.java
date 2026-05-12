package com.example.drug.service.supplier;

import com.example.drug.dto.CooperationRecordDTO;
import com.example.drug.util.Result;

/**
 * 供应商合作记录服务接口
 */
public interface SupplierCooperationService {
    
    /**
     * 添加合作记录
     */
    Result addCooperationRecord(CooperationRecordDTO dto);
    
    /**
     * 查询合作记录列表
     */
    Result list(String supplierId, String recordType, String startDate, String endDate,
                Integer pageNum, Integer pageSize);
    
    /**
     * 查询合作记录详情
     */
    Result getById(String recordId);
    
    /**
     * 删除合作记录
     */
    Result delete(String recordId);
}
