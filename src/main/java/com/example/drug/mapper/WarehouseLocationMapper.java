package com.example.drug.mapper;

import com.example.drug.entity.warehouse.WarehouseLocation;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 库位管理Mapper
 */
@Mapper
public interface WarehouseLocationMapper {

    // 分页条件查询库位
    @Select("<script>" +
            "SELECT l.*, w.warehouse_name FROM warehouse_location l " +
            "LEFT JOIN warehouse w ON l.warehouse_id = w.warehouse_id " +
            "WHERE 1=1 " +
            "<if test='warehouseId != null and warehouseId != \"\"'> AND l.warehouse_id = #{warehouseId} </if>" +
            "<if test='status != null and status != \"\"'> AND l.status = #{status} </if>" +
            "ORDER BY l.zone, l.shelf, l.level" +
            "</script>")
    List<WarehouseLocation> selectByCondition(@Param("warehouseId") String warehouseId, 
                                               @Param("status") String status);

    // 根据ID查询库位
    @Select("SELECT l.*, w.warehouse_name FROM warehouse_location l " +
            "LEFT JOIN warehouse w ON l.warehouse_id = w.warehouse_id " +
            "WHERE l.loc_id = #{locId}")
    WarehouseLocation selectById(String locId);

    // 新增库位
    @Insert("INSERT INTO warehouse_location(loc_id, loc_code, warehouse_id, zone, shelf, level, status) " +
            "VALUES(#{locId}, #{locCode}, #{warehouseId}, #{zone}, #{shelf}, #{level}, #{status})")
    int insert(WarehouseLocation location);

    // 修改库位
    @Update("UPDATE warehouse_location SET zone=#{zone}, shelf=#{shelf}, level=#{level}, status=#{status} WHERE loc_id=#{locId}")
    int update(WarehouseLocation location);

    // 绑定/解绑库存
    @Update("UPDATE warehouse_location SET status=#{status} WHERE loc_id=#{locId}")
    int updateStatus(@Param("locId") String locId, @Param("status") String status);

    // 获取某仓库下的最大库位编码
    @Select("SELECT MAX(loc_code) FROM warehouse_location WHERE warehouse_id = #{warehouseId} AND zone = #{zone} AND shelf = #{shelf}")
    String getMaxLocCode(@Param("warehouseId") String warehouseId, @Param("zone") String zone, @Param("shelf") String shelf);
}
