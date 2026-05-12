package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.supplier.SupplierCooperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 供应商合作记录Mapper
 */
@Mapper
public interface SupplierCooperationMapper extends BaseMapper<SupplierCooperation> {
    
    /**
     * 查询供应商合作记录列表
     */
    List<SupplierCooperation> selectCooperationList(@Param("supplierId") String supplierId,
                                                    @Param("recordType") String recordType,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);
    
    /**
     * 查询合作记录详情
     */
    SupplierCooperation selectCooperationDetail(@Param("recordId") String recordId);
}
