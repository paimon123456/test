package com.example.drug.controller;

import com.example.drug.entity.warehouse.Warehouse;
import com.example.drug.service.warehouse.WarehouseService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 仓库档案管理接口
 */
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    // 分页查询仓库列表（支持按名称和状态查询）
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String warehouseName,
                  @RequestParam(required = false) Integer status,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return warehouseService.list(warehouseName, status, pageNum, pageSize);
    }

    // 根据ID查询仓库详情
    @GetMapping("/{warehouseId}")
    public Result getById(@PathVariable String warehouseId) {
        return warehouseService.getById(warehouseId);
    }

    // 新增仓库
    @PostMapping("/add")
    public Result add(@RequestBody Warehouse warehouse) {
        return warehouseService.add(warehouse);
    }

    // 修改仓库
    @PostMapping("/update")
    public Result update(@RequestBody Warehouse warehouse) {
        return warehouseService.update(warehouse);
    }

    // 删除仓库
    @DeleteMapping("/delete/{warehouseId}")
    public Result delete(@PathVariable String warehouseId) {
        return warehouseService.delete(warehouseId);
    }
}
