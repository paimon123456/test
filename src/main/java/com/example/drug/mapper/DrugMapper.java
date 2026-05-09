package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 【统一】药品Mapper
 * 映射到 drug_info 表
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {
    
    @Update("ALTER TABLE drug_info AUTO_INCREMENT = 1")
    void resetAutoIncrement();
    
    @Update("UPDATE drug_info SET drug_id = #{newId} WHERE drug_id = #{oldId}")
    void updateDrugId(@Param("oldId") String oldId, @Param("newId") String newId);
    
    /**
     * 查询近效期药品（默认90天内）
     */
    @Select("SELECT * FROM drug_info WHERE status = 1")
    List<Drug> getAllActiveDrugs();
}
