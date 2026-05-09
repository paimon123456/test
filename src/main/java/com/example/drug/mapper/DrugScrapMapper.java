package com.example.drug.mapper;

import com.example.drug.entity.expiry.DrugScrap;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 药品报废Mapper
 */
@Mapper
public interface DrugScrapMapper {

    // 分页条件查询报废记录
    @Select("<script>" +
            "SELECT s.*, d.drug_name, d.specification " +
            "FROM drug_scrap s " +
            "LEFT JOIN drug_info d ON s.drug_id = d.drug_id " +
            "WHERE 1=1 " +
            "<if test='status != null and status != \"\"'> AND s.status = #{status} </if>" +
            "<if test='drugId != null and drugId != \"\"'> AND s.drug_id = #{drugId} </if>" +
            "ORDER BY s.scrap_id DESC" +
            "</script>")
    List<DrugScrap> selectByCondition(@Param("status") String status, @Param("drugId") String drugId);

    // 根据ID查询报废记录
    @Select("SELECT s.*, d.drug_name, d.specification " +
            "FROM drug_scrap s " +
            "LEFT JOIN drug_info d ON s.drug_id = d.drug_id " +
            "WHERE s.scrap_id = #{scrapId}")
    DrugScrap selectById(String scrapId);

    // 新增报废申请
    @Insert("INSERT INTO drug_scrap(scrap_id, scrap_no, inventory_id, drug_id, scrap_num, reason, status) " +
            "VALUES(#{scrapId}, #{scrapNo}, #{inventoryId}, #{drugId}, #{scrapNum}, #{reason}, #{status})")
    int insert(DrugScrap scrap);

    // 更新报废状态
    @Update("UPDATE drug_scrap SET status=#{status} WHERE scrap_id=#{scrapId}")
    int updateStatus(@Param("scrapId") String scrapId, @Param("status") String status);
}
