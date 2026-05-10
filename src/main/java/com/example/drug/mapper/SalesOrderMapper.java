package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.sales.SalesOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 销售订单Mapper
 */
@Mapper
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {
    
    /**
     * 查询销售订单列表（关联会员和收银员信息）
     */
    List<SalesOrder> selectOrderList(@Param("orderId") String orderId,
                                     @Param("memberId") String memberId,
                                     @Param("cashierId") String cashierId,
                                     @Param("status") String status,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);
    
    /**
     * 根据订单ID查询详情
     */
    SalesOrder selectOrderDetail(@Param("orderId") String orderId);
    
    /**
     * 统计销售数据（按日期）
     */
    List<Map<String, Object>> statSalesByDate(@Param("startDate") String startDate,
                                              @Param("endDate") String endDate);
    
    /**
     * 统计销售数据（按药品）
     */
    List<Map<String, Object>> statSalesByDrug(@Param("startDate") String startDate,
                                              @Param("endDate") String endDate);
    
    /**
     * 统计销售总额
     */
    BigDecimal statTotalAmount(@Param("startDate") String startDate,
                               @Param("endDate") String endDate);
}
