package com.example.drug.controller;

import com.example.drug.dto.TransferDTO;
import com.example.drug.service.warehouse.WarehouseTransferService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 移库管理接口
 */
@RestController
@RequestMapping("/transfer")
public class WarehouseTransferController {

    @Autowired
    private WarehouseTransferService transferService;

    // 分页条件查询移库记录
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String srcWareId,
                  @RequestParam(required = false) String destWareId,
                  @RequestParam(required = false) String status,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return transferService.list(srcWareId, destWareId, status, pageNum, pageSize);
    }

    // 根据ID查询移库详情
    @GetMapping("/{transferId}")
    public Result getById(@PathVariable String transferId) {
        return transferService.getById(transferId);
    }

    // 提交移库申请
    @PostMapping("/apply")
    public Result apply(@RequestBody TransferDTO dto) {
        return transferService.apply(dto);
    }

    // 审核移库申请
    @PostMapping("/audit")
    public Result audit(@RequestParam String transferId, @RequestParam String status, @RequestParam String auditorId) {
        return transferService.audit(transferId, status, auditorId);
    }

    // 完成移库
    @PostMapping("/finish")
    public Result finish(@RequestParam String transferId) {
        return transferService.finish(transferId);
    }
}
