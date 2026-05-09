package com.example.drug.service.expiry;

import com.example.drug.util.Result;

public interface DrugExpiryRemindService {
    // 分页查询提醒列表（支持按状态、预警天数筛选）
    Result list(String status, Integer thresholdDays, Integer pageNum, Integer pageSize);
    // 处理提醒
    Result handle(String remindId, String status, String operatorId);
    // 定时任务：生成近效期提醒
    void generateRemind();
}
