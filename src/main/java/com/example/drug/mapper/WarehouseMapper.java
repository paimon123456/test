package com.example.drug.mapper;

import com.example.drug.entity.warehouse.Warehouse;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 仓库档案Mapper
 */
@Mapper
public interface WarehouseMapper {

    // 分页条件查询仓库（支持按名称和状态模糊查询）
    @Select("<script>" +
            "SELECT * FROM warehouse WHERE 1=1 " +
            "<if test='warehouseName != null and warehouseName != \"\"'> " +
            "AND warehouse_name LIKE CONCAT('%', #{warehouseName}, '%') " +
            "</if>" +
            "<if test='status != null'> " +
            "AND status = #{status} " +
            "</if>" +
            "ORDER BY warehouse_code" +
            "</script>")
    List<Warehouse> selectByCondition(@Param("warehouseName") String warehouseName, @Param("status") Integer status);

    // 根据ID查询仓库
    @Select("SELECT * FROM warehouse WHERE warehouse_id = #{warehouseId}")
    Warehouse selectById(String warehouseId);

    // 新增仓库
    @Insert("INSERT INTO warehouse(warehouse_id, warehouse_code, warehouse_name, location, manager_id, status) " +
            "VALUES(#{warehouseId}, #{warehouseCode}, #{warehouseName}, #{location}, #{managerId}, #{status})")
    int insert(Warehouse warehouse);

    // 修改仓库
    @Update("UPDATE warehouse SET warehouse_code=#{warehouseCode}, warehouse_name=#{warehouseName}, location=#{location}, manager_id=#{managerId}, status=#{status} WHERE warehouse_id=#{warehouseId}")
    int update(Warehouse warehouse);

    // 删除仓库（物理删除）
    @Delete("DELETE FROM warehouse WHERE warehouse_id=#{warehouseId}")
    int deleteById(String warehouseId);

    // 获取下一个仓库编码
    @Select("SELECT MAX(CAST(SUBSTRING(warehouse_code, 2) AS UNSIGNED)) FROM warehouse")
    Integer getMaxWarehouseCode();
}
