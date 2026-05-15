package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.purchase.PurchaseOrder;
import com.example.drug.entity.purchase.PurchaseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单Mapper
 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
    
    /**
     * 查询采购订单列表（关联查询）
     */
    List<PurchaseOrder> selectPurchaseOrderList(@Param("orderNo") String orderNo,
                                                @Param("supplierId") String supplierId,
                                                @Param("status") String status,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);
    
    /**
     * 查询采购订单详情
     */
    PurchaseOrder selectPurchaseOrderDetail(@Param("orderId") String orderId);
    
    /**
     * 查询采购订单明细
     */
    List<PurchaseItem> selectPurchaseItems(@Param("orderId") String orderId);
    
    /**
     * 统计待审核采购单数量
     */
    Integer countPendingOrders();
    
    /**
     * 统计采购总金额
     */
    BigDecimal sumPurchaseAmount(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
