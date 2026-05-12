package com.example.drug.service.supplier;

import com.example.drug.dto.SupplierDTO;
import com.example.drug.entity.supplier.Supplier;
import com.example.drug.util.Result;

/**
 * 供应商服务接口
 */
public interface SupplierService {
    
    /**
     * 新增供应商
     */
    Result addSupplier(SupplierDTO dto);
    
    /**
     * 修改供应商
     */
    Result updateSupplier(SupplierDTO dto);
    
    /**
     * 删除供应商
     */
    Result deleteSupplier(String supplierId);
    
    /**
     * 查询供应商列表
     */
    Result list(String supplierName, String qualificationStatus, String cooperationStatus,
                Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 查询供应商详情
     */
    Result getById(String supplierId);
    
    /**
     * 根据编码查询
     */
    Result getByCode(String supplierCode);
    
    /**
     * 资质过期提醒
     */
    Result qualificationReminder(Integer days);
    
    /**
     * 更新资质状态
     */
    Result updateQualificationStatus();
}
