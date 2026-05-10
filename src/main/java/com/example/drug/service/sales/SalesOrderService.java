package com.example.drug.service.sales;

import com.example.drug.dto.SalesOrderDTO;
import com.example.drug.util.Result;

/**
 * 销售订单服务接口
 */
public interface SalesOrderService {
    
    /**
     * 前台销售开单
     */
    Result createSalesOrder(SalesOrderDTO dto);
    
    /**
     * 查询销售订单列表
     */
    Result list(String orderId, String memberId, String cashierId, String status,
                String startDate, String endDate, Integer pageNum, Integer pageSize);
    
    /**
     * 根据订单ID查询详情（包含明细）
     */
    Result getById(String orderId);
    
    /**
     * 销售数据统计（按日期）
     */
    Result statByDate(String startDate, String endDate);
    
    /**
     * 销售数据统计（按药品）
     */
    Result statByDrug(String startDate, String endDate);
    
    /**
     * 销售总额统计
     */
    Result statTotalAmount(String startDate, String endDate);
}
