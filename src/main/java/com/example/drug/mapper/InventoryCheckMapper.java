package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.inventory.InventoryCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存盘点Mapper
 */
@Mapper
public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {
    
    /**
     * 查询盘点单列表（关联药品信息）
     */
    List<InventoryCheck> selectCheckList(@Param("drugName") String drugName,
                                         @Param("auditStatus") String auditStatus,
                                         @Param("startDate") String startDate,
                                         @Param("endDate") String endDate);
    
    /**
     * 根据盘点单ID查询详情
     */
    InventoryCheck selectCheckDetail(@Param("checkId") String checkId);
}
