package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.sales.SalesReturn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售退货Mapper
 */
@Mapper
public interface SalesReturnMapper extends BaseMapper<SalesReturn> {
    
    /**
     * 查询退货单列表
     */
    List<SalesReturn> selectReturnList(@Param("originalOrderId") String originalOrderId,
                                       @Param("drugName") String drugName,
                                       @Param("status") String status,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);
    
    /**
     * 根据退货单ID查询详情
     */
    SalesReturn selectReturnDetail(@Param("returnId") String returnId);
}
