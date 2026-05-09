package com.example.drug.mapper;

import com.example.drug.entity.inventory.Inventory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 库存Mapper - 注解方式
 */
@Mapper
public interface InventoryMapper {

    // 分页条件查询库存（动态计算状态）
    @Select("<script>" +
            "SELECT di.*, d.drug_name, d.specification, d.manufacturer, w.warehouse_name, " +
            "wl.zone, wl.shelf, wl.level, " +
            "DATEDIFF(di.expiry_date, NOW()) as remain_days, " +
            "CASE " +
            "  WHEN DATEDIFF(di.expiry_date, NOW()) &lt;= 0 THEN '过期' " +
            "  WHEN DATEDIFF(di.expiry_date, NOW()) &lt;= 30 THEN '近效期' " +
            "  WHEN DATEDIFF(di.expiry_date, NOW()) &lt;= 90 THEN '临期' " +
            "  ELSE '正常' " +
            "END as dynamic_status " +
            "FROM drug_inventory di " +
            "LEFT JOIN drug_info d ON di.drug_id = d.drug_id " +
            "LEFT JOIN warehouse w ON di.warehouse_id = w.warehouse_id " +
            "LEFT JOIN warehouse_location wl ON di.location = CONCAT(wl.zone, '-', wl.shelf, '-', wl.level) " +
            "WHERE 1=1 " +
            "AND d.drug_name IS NOT NULL AND d.drug_name != '' " +
            "<if test='warehouseId != null and warehouseId != \"\"'> AND di.warehouse_id = #{warehouseId} </if>" +
            "<if test='drugName != null and drugName != \"\"'> AND d.drug_name LIKE CONCAT('%', #{drugName}, '%') </if>" +
            "<if test='status != null and status != \"\"'> " +
            "  AND (di.status = #{status} OR " +
            "    ( #{status} = '近效期' AND DATEDIFF(di.expiry_date, NOW()) > 0 AND DATEDIFF(di.expiry_date, NOW()) &lt;= 30 ) " +
            "    OR ( #{status} = '过期' AND DATEDIFF(di.expiry_date, NOW()) &lt;= 0 ) " +
            "    OR ( #{status} = '正常' AND DATEDIFF(di.expiry_date, NOW()) > 90 ) " +
            "  )" +
            "</if>" +
            "ORDER BY di.expiry_date ASC" +
            "</script>")
    List<Inventory> selectByCondition(@Param("warehouseId") String warehouseId,
                                       @Param("drugName") String drugName,
                                       @Param("status") String status);

    // 根据ID查询库存详情
    @Select("SELECT di.*, d.drug_name, d.specification, d.manufacturer, w.warehouse_name, " +
            "wl.zone, wl.shelf, wl.level " +
            "FROM drug_inventory di " +
            "LEFT JOIN drug_info d ON di.drug_id = d.drug_id " +
            "LEFT JOIN warehouse w ON di.warehouse_id = w.warehouse_id " +
            "LEFT JOIN warehouse_location wl ON di.location = CONCAT(wl.zone, '-', wl.shelf, '-', wl.level) " +
            "WHERE di.inventory_id = #{inventoryId}")
    Inventory selectById(String inventoryId);

    // 根据仓库ID查询库存列表
    @Select("SELECT di.*, d.drug_name, d.specification, d.manufacturer " +
            "FROM drug_inventory di " +
            "LEFT JOIN drug_info d ON di.drug_id = d.drug_id " +
            "WHERE di.warehouse_id = #{warehouseId} AND di.status = '正常' AND di.stock_num > 0")
    List<Inventory> selectByWarehouseId(String warehouseId);

    // 查询近效期药品（包含已过期但未报废的）
    @Select("SELECT di.*, d.drug_name, d.specification, d.manufacturer, w.warehouse_name, " +
            "wl.zone, wl.shelf, wl.level, " +
            "DATEDIFF(di.expiry_date, NOW()) as remain_days " +
            "FROM drug_inventory di " +
            "LEFT JOIN drug_info d ON di.drug_id = d.drug_id " +
            "LEFT JOIN warehouse w ON di.warehouse_id = w.warehouse_id " +
            "LEFT JOIN warehouse_location wl ON di.location = CONCAT(wl.zone, '-', wl.shelf, '-', wl.level) " +
            "WHERE DATEDIFF(di.expiry_date, NOW()) &lt;= #{days} AND di.stock_num > 0 AND d.drug_name IS NOT NULL AND d.drug_name != ''")
    List<Inventory> selectNearExpiry(@Param("days") Integer days);

    // 锁定过期药品状态
    @Update("UPDATE drug_inventory SET status = '锁定' WHERE expiry_date < NOW() AND status != '锁定'")
    int lockExpiredDrugs();

    // 更新库存状态
    @Update("UPDATE drug_inventory SET status = #{status} WHERE inventory_id = #{inventoryId}")
    int updateStatus(@Param("inventoryId") String inventoryId, @Param("status") String status);

    // 减少库存数量（报废用）
    @Update("UPDATE drug_inventory SET stock_num = stock_num - #{scrapNum} WHERE inventory_id = #{inventoryId} AND stock_num >= #{scrapNum}")
    int reduceStock(@Param("inventoryId") String inventoryId, @Param("scrapNum") Integer scrapNum);
}
