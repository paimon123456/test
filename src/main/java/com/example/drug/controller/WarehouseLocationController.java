package com.example.drug.controller;

import com.example.drug.entity.warehouse.WarehouseLocation;
import com.example.drug.service.warehouse.WarehouseLocationService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 库位管理接口
 */
@RestController
@RequestMapping("/location")
public class WarehouseLocationController {

    @Autowired
    private WarehouseLocationService locationService;

    // 分页条件查询库位
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String warehouseId,
                  @RequestParam(required = false) String status,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return locationService.list(warehouseId, status, pageNum, pageSize);
    }

    // 根据ID查询库位详情
    @GetMapping("/{locId}")
    public Result getById(@PathVariable String locId) {
        return locationService.getById(locId);
    }

    // 新增库位
    @PostMapping("/add")
    public Result add(@RequestBody WarehouseLocation location) {
        return locationService.add(location);
    }

    // 修改库位
    @PostMapping("/update")
    public Result update(@RequestBody WarehouseLocation location) {
        return locationService.update(location);
    }

    // 绑定/解绑库存
    @PostMapping("/bind")
    public Result bindInventory(@RequestParam String locId, @RequestParam String status) {
        return locationService.bindInventory(locId, status);
    }
}
