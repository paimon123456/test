package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.inventory.PurchaseInItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 采购入库明细Mapper
 */
@Mapper
public interface PurchaseInItemMapper extends BaseMapper<PurchaseInItem> {
    
    /**
     * 根据入库单ID查询明细
     */
    List<PurchaseInItem> selectByInId(@Param("inId") String inId);
    
    /**
     * 批量插入明细
     */
    int batchInsert(@Param("items") List<PurchaseInItem> items);
}
