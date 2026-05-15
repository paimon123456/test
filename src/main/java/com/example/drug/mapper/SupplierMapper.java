package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.supplier.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 供应商Mapper
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
    
    /**
     * 查询供应商列表（关联查询）
     */
    List<Supplier> selectSupplierList(@Param("supplierName") String supplierName,
                                      @Param("qualificationStatus") String qualificationStatus,
                                      @Param("cooperationStatus") String cooperationStatus,
                                      @Param("status") Integer status);
    
    /**
     * 查询供应商详情
     */
    Supplier selectSupplierDetail(@Param("supplierId") String supplierId);
    
    /**
     * 查询资质即将过期的供应商
     */
    List<Supplier> selectExpiringSuppliers(@Param("days") Integer days);
    
    /**
     * 根据供应商编码查询
     */
    Supplier selectByCode(@Param("supplierCode") String supplierCode);
}
