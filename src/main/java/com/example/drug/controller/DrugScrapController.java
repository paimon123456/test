package com.example.drug.controller;

import com.example.drug.dto.ScrapDTO;
import com.example.drug.service.expiry.DrugScrapService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 药品报废管理接口
 */
@RestController
@RequestMapping("/scrap")
public class DrugScrapController {

    @Autowired
    private DrugScrapService scrapService;

    // 分页条件查询报废记录
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String status,
                  @RequestParam(required = false) String drugId,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return scrapService.list(status, drugId, pageNum, pageSize);
    }

    // 根据ID查询报废详情
    @GetMapping("/{scrapId}")
    public Result getById(@PathVariable String scrapId) {
        return scrapService.getById(scrapId);
    }

    // 提交报废申请
    @PostMapping("/apply")
    public Result apply(@RequestBody ScrapDTO dto) {
        return scrapService.apply(dto);
    }

    // 审核报废申请
    @PostMapping("/audit")
    public Result audit(@RequestParam String scrapId, @RequestParam String status, @RequestParam String auditorId) {
        return scrapService.audit(scrapId, status, auditorId);
    }

    // 审批报废申请
    @PostMapping("/approve")
    public Result approve(@RequestParam String scrapId, @RequestParam String status, @RequestParam String approverId) {
        return scrapService.approve(scrapId, status, approverId);
    }

    // 执行报废
    @PostMapping("/execute")
    public Result execute(@RequestParam String scrapId, @RequestParam(required = false) String outboundId) {
        return scrapService.execute(scrapId, outboundId);
    }
}
