package com.example.drug.mapper;

import com.example.drug.entity.expiry.DrugExpiryRemind;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 效期提醒Mapper
 */
@Mapper
public interface DrugExpiryRemindMapper {

    // 分页条件查询提醒列表
    @Select("<script>" +
            "SELECT r.*, d.drug_name, d.specification, i.batch_no, i.expiry_date, i.stock_num " +
            "FROM drug_expiry_remind r " +
            "LEFT JOIN drug_info d ON r.drug_id = d.drug_id " +
            "LEFT JOIN drug_inventory i ON r.inventory_id = i.inventory_id " +
            "WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "<if test='thresholdDays != null'> AND r.remain_days &lt;= #{thresholdDays} </if>" +
            "ORDER BY r.remain_days ASC" +
            "</script>")
    List<DrugExpiryRemind> selectByCondition(@Param("status") String status,
                                             @Param("thresholdDays") Integer thresholdDays);

    // 新增提醒记录
    @Insert("INSERT INTO drug_expiry_remind(remind_id, inventory_id, drug_id, threshold_days, remain_days, status) " +
            "VALUES(#{remindId}, #{inventoryId}, #{drugId}, #{thresholdDays}, #{remainDays}, #{status})")
    int insert(DrugExpiryRemind remind);

    // 处理提醒
    @Update("UPDATE drug_expiry_remind SET status=#{status} WHERE remind_id=#{remindId}")
    int handle(@Param("remindId") String remindId, @Param("status") String status);

    // 批量查询近效期药品（包含已过期但未报废的）
    @Select("SELECT inventory_id, drug_id, DATEDIFF(expiry_date, NOW()) as remain_days FROM drug_inventory " +
            "WHERE DATEDIFF(expiry_date, NOW()) <= #{thresholdDays} AND stock_num > 0")
    List<DrugExpiryRemind> selectNearExpiry(@Param("thresholdDays") Integer thresholdDays);

    // 查询已存在未处理提醒的inventory_id列表（用于去重）
    @Select("SELECT inventory_id FROM drug_expiry_remind WHERE status = '未处理'")
    List<String> selectExistingInventoryIds();
}
