package com.example.drug.controller;

import com.example.drug.entity.price.PriceAlertConfig;
import com.example.drug.service.price.PriceAlertService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price-alert")
public class PriceAlertController {

    @Autowired
    private PriceAlertService priceAlertService;

    // =================== 预警配置 ===================

    @GetMapping("/configs")
    public Result getAlertConfigs() {
        return priceAlertService.getAlertConfigs();
    }

    @PostMapping("/config/update")
    public Result updateAlertConfig(@RequestBody PriceAlertConfig config) {
        return priceAlertService.updateAlertConfig(config);
    }

    // =================== 预警日志 ===================

    @GetMapping("/logs")
    public Result getAlertLogs(@RequestParam(required = false) String drugId,
                                @RequestParam(required = false) String alertType,
                                @RequestParam(required = false) String handleStatus,
                                @RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate) {
        return priceAlertService.getAlertLogs(drugId, alertType, handleStatus, startDate, endDate);
    }

    @GetMapping("/log/{alertId}")
    public Result getAlertLogDetail(@PathVariable String alertId) {
        return priceAlertService.getAlertLogDetail(alertId);
    }

    @PostMapping("/log/handle")
    public Result handleAlert(@RequestParam String alertId,
                               @RequestParam String handleStatus,
                               @RequestParam(required = false) String handler,
                               @RequestParam(required = false) String handleRemark) {
        return priceAlertService.handleAlert(alertId, handleStatus, handler, handleRemark);
    }

    @GetMapping("/log/unhandled-count")
    public Result getUnhandledCount() {
        return priceAlertService.getUnhandledCount();
    }

    // =================== 手动扫描 ===================

    @PostMapping("/scan")
    public Result manualScan() {
        try {
            priceAlertService.scanAndAlert();
            return Result.success("预警扫描完成，请查看预警日志");
        } catch (Exception e) {
            return Result.fail("预警扫描失败：" + e.getMessage());
        }
    }
}
