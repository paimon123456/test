package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.purchase.InventoryReserve;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 库存预占Mapper
 */
@Mapper
public interface InventoryReserveMapper extends BaseMapper<InventoryReserve> {
    
    /**
     * 根据单号查询预占记录
     */
    List<InventoryReserve> selectByOrderId(@Param("orderId") String orderId);
    
    /**
     * 根据单号类型查询
     */
    List<InventoryReserve> selectByOrderType(@Param("orderType") String orderType);
    
    /**
     * 释放过期预占
     */
    int releaseExpiredReserves();
}
