package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.purchase.PurchaseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购订单明细Mapper
 */
@Mapper
public interface PurchaseItemMapper extends BaseMapper<PurchaseItem> {
    
    /**
     * 根据订单ID查询明细
     */
    List<PurchaseItem> selectItemsByOrderId(@Param("orderId") String orderId);
    
    /**
     * 批量插入采购明细
     */
    int batchInsert(@Param("items") List<PurchaseItem> items);
}
