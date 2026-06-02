package com.example.drug.mapper;

import com.example.drug.entity.price.PriceAlertConfig;
import com.example.drug.entity.price.PriceAlertLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriceAlertMapper {

    // =================== 预警配置 ===================
    List<PriceAlertConfig> getAlertConfigs();

    PriceAlertConfig getAlertConfigByType(@Param("alertType") String alertType);

    int saveAlertConfig(PriceAlertConfig config);

    int updateAlertConfig(PriceAlertConfig config);

    // =================== 预警日志 ===================
    List<PriceAlertLog> getAlertLogs(@Param("drugId") String drugId,
                                    @Param("alertType") String alertType,
                                    @Param("handleStatus") String handleStatus,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate);

    PriceAlertLog getAlertLogById(@Param("alertId") String alertId);

    int saveAlertLog(PriceAlertLog log);

    int updateAlertLogHandle(@Param("alertId") String alertId,
                             @Param("handleStatus") String handleStatus,
                             @Param("handler") String handler,
                             @Param("handleRemark") String handleRemark);

    int countUnhandledAlert();

    // =================== 价格预警扫描 ===================
    List<PriceAlertLog> findSellBelowPurchase(@Param("thresholdPercent") java.math.BigDecimal thresholdPercent);

    List<PriceAlertLog> findPurchaseSurge(@Param("thresholdPercent") java.math.BigDecimal thresholdPercent);

    List<PriceAlertLog> findRetailSurge(@Param("thresholdPercent") java.math.BigDecimal thresholdPercent);
}
