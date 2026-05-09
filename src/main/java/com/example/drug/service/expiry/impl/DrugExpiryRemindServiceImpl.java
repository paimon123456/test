package com.example.drug.service.expiry.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.expiry.DrugExpiryRemind;
import com.example.drug.mapper.DrugExpiryRemindMapper;
import com.example.drug.service.expiry.DrugExpiryRemindService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DrugExpiryRemindServiceImpl implements DrugExpiryRemindService {

    @Autowired
    private DrugExpiryRemindMapper remindMapper;

    // 近效期阈值：30天
    private static final Integer THRESHOLD_DAYS = 30;

    @Override
    public Result list(String status, Integer thresholdDays, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<DrugExpiryRemind> list = remindMapper.selectByCondition(status, thresholdDays);
        PageInfo<DrugExpiryRemind> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result handle(String remindId, String status, String operatorId) {
        int row = remindMapper.handle(remindId, status);
        return row > 0 ? Result.success("处理成功") : Result.fail("处理失败");
    }

    /**
     * 定时任务：每日凌晨2点自动生成近效期提醒
     * 优化：避免重复生成已存在的提醒
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateRemind() {
        System.out.println("========== 开始执行近效期提醒定时任务 ==========");

        // 1. 查询近效期药品
        List<DrugExpiryRemind> nearExpiryList = remindMapper.selectNearExpiry(THRESHOLD_DAYS);

        // 2. 查询已存在的未处理提醒的inventory_id
        List<String> existingInventoryIds = remindMapper.selectExistingInventoryIds();

        // 3. 批量生成提醒记录（过滤已存在的）
        int generateCount = 0;
        for (DrugExpiryRemind remind : nearExpiryList) {
            // 跳过已存在未处理提醒的库存
            if (existingInventoryIds.contains(remind.getInventoryId())) {
                continue;
            }

            remind.setRemindId(UUIDUtil.getUUID());
            remind.setThresholdDays(THRESHOLD_DAYS);
            remind.setStatus("未处理");
            remindMapper.insert(remind);
            generateCount++;
        }

        System.out.println("========== 近效期提醒定时任务执行完成，扫描到 " + nearExpiryList.size() +
                " 条近效期药品，新增 " + generateCount + " 条提醒 ==========");
    }
}
