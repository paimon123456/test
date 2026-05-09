package com.example.drug.mapper;

import com.example.drug.entity.warehouse.WarehouseTransfer;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 移库管理Mapper
 */
@Mapper
public interface WarehouseTransferMapper {

    // 分页条件查询移库记录
    @Select("<script>" +
            "SELECT t.*, sw.warehouse_name as src_ware_name, dw.warehouse_name as dest_ware_name " +
            "FROM warehouse_transfer t " +
            "LEFT JOIN warehouse sw ON t.src_ware_id = sw.warehouse_id " +
            "LEFT JOIN warehouse dw ON t.dest_ware_id = dw.warehouse_id " +
            "WHERE 1=1 " +
            "<if test='srcWareId != null and srcWareId != \"\"'> AND t.src_ware_id = #{srcWareId} </if>" +
            "<if test='destWareId != null and destWareId != \"\"'> AND t.dest_ware_id = #{destWareId} </if>" +
            "<if test='status != null and status != \"\"'> AND t.status = #{status} </if>" +
            "ORDER BY t.transfer_id DESC" +
            "</script>")
    List<WarehouseTransfer> selectByCondition(@Param("srcWareId") String srcWareId,
                                               @Param("destWareId") String destWareId,
                                               @Param("status") String status);

    // 根据ID查询移库记录
    @Select("SELECT t.*, sw.warehouse_name as src_ware_name, dw.warehouse_name as dest_ware_name " +
            "FROM warehouse_transfer t " +
            "LEFT JOIN warehouse sw ON t.src_ware_id = sw.warehouse_id " +
            "LEFT JOIN warehouse dw ON t.dest_ware_id = dw.warehouse_id " +
            "WHERE t.transfer_id = #{transferId}")
    WarehouseTransfer selectById(String transferId);

    // 新增移库申请
    @Insert("INSERT INTO warehouse_transfer(transfer_id, transfer_no, src_ware_id, dest_ware_id, inventory_id, transfer_num, status) " +
            "VALUES(#{transferId}, #{transferNo}, #{srcWareId}, #{destWareId}, #{inventoryId}, #{transferNum}, #{status})")
    int insert(WarehouseTransfer transfer);

    // 更新移库状态
    @Update("UPDATE warehouse_transfer SET status=#{status} WHERE transfer_id=#{transferId}")
    int updateStatus(@Param("transferId") String transferId, @Param("status") String status);
}
