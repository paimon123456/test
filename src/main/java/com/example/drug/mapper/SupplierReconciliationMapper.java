package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.supplier.SupplierReconciliation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 供应商对账Mapper
 */
@Mapper
public interface SupplierReconciliationMapper extends BaseMapper<SupplierReconciliation> {
    
    /**
     * 查询对账列表
     */
    List<SupplierReconciliation> selectReconciliationList(@Param("supplierId") String supplierId,
                                                            @Param("status") String status,
                                                            @Param("startDate") String startDate,
                                                            @Param("endDate") String endDate);
    
    /**
     * 查询对账详情
     */
    SupplierReconciliation selectReconciliationDetail(@Param("billId") String billId);
}
