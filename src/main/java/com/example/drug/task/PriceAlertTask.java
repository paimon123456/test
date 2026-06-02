package com.example.drug.task;

import com.example.drug.service.price.PriceAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 价格预警定时任务
 * 每30分钟自动扫描一次，检测售价低于进价、进价异常上涨等异常情况
 */
@Component
public class PriceAlertTask {

    @Autowired
    private PriceAlertService priceAlertService;

    /**
     * 每30分钟执行一次价格预警扫描
     * cron: 秒 分 时 日 月 周
/*
     * 0 *//*
30 * * * ?  每30分钟执行
*/

    @Scheduled(cron = "0 */30 * * * ?")
    public void autoScanPriceAlerts() {
        priceAlertService.scanAndAlert();
    }
}
