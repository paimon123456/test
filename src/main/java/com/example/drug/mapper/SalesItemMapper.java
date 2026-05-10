package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.sales.SalesItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售明细Mapper
 */
@Mapper
public interface SalesItemMapper extends BaseMapper<SalesItem> {
    
    /**
     * 根据订单ID查询明细列表
     */
    List<SalesItem> selectItemsByOrderId(@Param("orderId") String orderId);
    
    /**
     * 查询销售明细列表（关联药品信息）
     */
    List<SalesItem> selectItemList(@Param("orderId") String orderId,
                                   @Param("drugName") String drugName);
}
