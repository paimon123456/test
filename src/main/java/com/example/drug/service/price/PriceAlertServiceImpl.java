package com.example.drug.service.price;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.price.PriceAlertConfig;
import com.example.drug.entity.price.PriceAlertLog;
import com.example.drug.mapper.PriceAlertMapper;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceAlertServiceImpl implements PriceAlertService {

    @Autowired
    private PriceAlertMapper priceAlertMapper;

    @Override
    public Result getAlertConfigs() {
        try {
            List<PriceAlertConfig> configs = priceAlertMapper.getAlertConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            return Result.fail("获取预警配置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAlertConfig(PriceAlertConfig config) {
        try {
            PriceAlertConfig existing = priceAlertMapper.getAlertConfigByType(config.getAlertType());
            if (existing != null) {
                config.setConfigId(existing.getConfigId());
                priceAlertMapper.updateAlertConfig(config);
            } else {
                config.setConfigId(UUIDUtil.getUUID());
                priceAlertMapper.saveAlertConfig(config);
            }
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("更新预警配置失败：" + e.getMessage());
        }
    }

    @Override
    public Result getAlertLogs(String drugId, String alertType, String handleStatus,
                                String startDate, String endDate) {
        try {
            List<PriceAlertLog> logs = priceAlertMapper.getAlertLogs(
                    drugId, alertType, handleStatus, startDate, endDate);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.fail("获取预警日志失败：" + e.getMessage());
        }
    }

    @Override
    public Result getAlertLogDetail(String alertId) {
        try {
            PriceAlertLog log = priceAlertMapper.getAlertLogById(alertId);
            if (log == null) {
                return Result.fail("预警记录不存在");
            }
            return Result.success(log);
        } catch (Exception e) {
            return Result.fail("获取预警详情失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result handleAlert(String alertId, String handleStatus, String handler, String handleRemark) {
        try {
            priceAlertMapper.updateAlertLogHandle(alertId, handleStatus, handler, handleRemark);
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("处理预警失败：" + e.getMessage());
        }
    }

    @Override
    public Result getUnhandledCount() {
        try {
            int count = priceAlertMapper.countUnhandledAlert();
            return Result.success(count);
        } catch (Exception e) {
            return Result.fail("获取未处理预警数失败：" + e.getMessage());
        }
    }

    /**
     * 定时扫描：检查售价低于进价 和 进价异常上涨 两种情况
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAndAlert() {
        System.out.println("----- 价格预警扫描任务开始 -----");
        try {
            // 1. 扫描售价低于进价
            PriceAlertConfig sellConfig = priceAlertMapper.getAlertConfigByType("SELL_BELOW_PURCHASE");
            if (sellConfig != null && sellConfig.getEnableStatus() == 1) {
                BigDecimal threshold = sellConfig.getThresholdPercent() != null
                        ? sellConfig.getThresholdPercent() : BigDecimal.ZERO;
                List<PriceAlertLog> sellAlerts = priceAlertMapper.findSellBelowPurchase(threshold);
                for (PriceAlertLog alert : sellAlerts) {
                    alert.setAlertId(UUIDUtil.getUUID());
                    priceAlertMapper.saveAlertLog(alert);
                    System.out.println("  [售价低于进价预警] " + alert.getAlertContent());
                }
                System.out.println("  扫描售价低于进价完成，新增预警 " + sellAlerts.size() + " 条");
            } else {
                System.out.println("  售价低于进价预警未启用，跳过");
            }

            // 2. 扫描进价异常上涨
            PriceAlertConfig surgeConfig = priceAlertMapper.getAlertConfigByType("PURCHASE_SURGE");
            if (surgeConfig != null && surgeConfig.getEnableStatus() == 1) {
                BigDecimal threshold = surgeConfig.getThresholdPercent() != null
                        ? surgeConfig.getThresholdPercent() : new BigDecimal("20");
                List<PriceAlertLog> surgeAlerts = priceAlertMapper.findPurchaseSurge(threshold);
                for (PriceAlertLog alert : surgeAlerts) {
                    alert.setAlertId(UUIDUtil.getUUID());
                    priceAlertMapper.saveAlertLog(alert);
                    System.out.println("  [进价异常上涨预警] " + alert.getAlertContent());
                }
                System.out.println("  扫描进价异常上涨完成，新增预警 " + surgeAlerts.size() + " 条");
            } else {
                System.out.println("  进价异常上涨预警未启用，跳过");
            }

            // 3. 扫描售价异常上涨
            PriceAlertConfig retailSurgeConfig = priceAlertMapper.getAlertConfigByType("RETAIL_SURGE");
            if (retailSurgeConfig != null && retailSurgeConfig.getEnableStatus() == 1) {
                BigDecimal threshold = retailSurgeConfig.getThresholdPercent() != null
                        ? retailSurgeConfig.getThresholdPercent() : new BigDecimal("50");
                List<PriceAlertLog> retailSurgeAlerts = priceAlertMapper.findRetailSurge(threshold);
                for (PriceAlertLog alert : retailSurgeAlerts) {
                    alert.setAlertId(UUIDUtil.getUUID());
                    priceAlertMapper.saveAlertLog(alert);
                    System.out.println("  [售价异常上涨预警] " + alert.getAlertContent());
                }
                System.out.println("  扫描售价异常上涨完成，新增预警 " + retailSurgeAlerts.size() + " 条");
            } else {
                System.out.println("  售价异常上涨预警未启用，跳过");
            }
        } catch (Exception e) {
            System.err.println("价格预警扫描异常：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("----- 价格预警扫描任务结束 -----");
    }
}
