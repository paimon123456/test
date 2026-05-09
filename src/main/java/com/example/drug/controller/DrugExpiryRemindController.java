package com.example.drug.controller;

import com.example.drug.service.expiry.DrugExpiryRemindService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 效期提醒管理接口
 */
@RestController
@RequestMapping("/remind")
public class DrugExpiryRemindController {

    @Autowired
    private DrugExpiryRemindService remindService;

    // 分页条件查询提醒列表
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String status,
                  @RequestParam(required = false) Integer thresholdDays,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return remindService.list(status, thresholdDays, pageNum, pageSize);
    }

    // 处理提醒
    @PostMapping("/handle")
    public Result handle(@RequestParam String remindId, @RequestParam String status, @RequestParam String operatorId) {
        return remindService.handle(remindId, status, operatorId);
    }

    // 手动触发定时任务（测试用）
    @PostMapping("/generate")
    public Result generate() {
        remindService.generateRemind();
        return Result.success("提醒生成成功");
    }
}
