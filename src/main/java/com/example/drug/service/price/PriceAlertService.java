package com.example.drug.service.price;

import com.example.drug.entity.price.PriceAlertConfig;
import com.example.drug.entity.price.PriceAlertLog;
import com.example.drug.util.Result;

import java.util.List;

public interface PriceAlertService {

    Result getAlertConfigs();

    Result updateAlertConfig(PriceAlertConfig config);

    Result getAlertLogs(String drugId, String alertType, String handleStatus,
                        String startDate, String endDate);

    Result getAlertLogDetail(String alertId);

    Result handleAlert(String alertId, String handleStatus, String handler, String handleRemark);

    Result getUnhandledCount();

    /** 定时任务：扫描并生成价格预警 */
    void scanAndAlert();
}
