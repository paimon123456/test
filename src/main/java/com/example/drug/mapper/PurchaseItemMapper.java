package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.purchase.PurchaseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 采购明细Mapper
 */
@Mapper
public interface PurchaseItemMapper extends BaseMapper<PurchaseItem> {
    
    /**
     * 根据订单ID删除所有明细
     */
    int deleteByOrderId(@Param("orderId") String orderId);
    
    /**
     * 批量插入明细
     */
    int batchInsert(@Param("items") java.util.List<PurchaseItem> items);
}
