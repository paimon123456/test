package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.report.DailyReport;
import com.example.drug.entity.report.InventoryTurnover;
import com.example.drug.entity.report.SalesRanking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper extends BaseMapper<Object> {

    List<DailyReport> getDailyReport(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      @Param("drugId") String drugId);

    List<DailyReport> getMonthlyReport(@Param("startMonth") String startMonth,
                                        @Param("endMonth") String endMonth,
                                        @Param("drugId") String drugId);

    List<SalesRanking> getSalesRanking(@Param("startDate") String startDate,
                                       @Param("endDate") String endDate,
                                       @Param("limit") Integer limit);

    List<InventoryTurnover> getInventoryTurnover(@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate,
                                                  @Param("drugId") String drugId);

    Integer getTotalSalesAmount(@Param("startDate") String startDate,
                                @Param("endDate") String endDate);
}
