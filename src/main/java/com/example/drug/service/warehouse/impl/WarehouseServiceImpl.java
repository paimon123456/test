package com.example.drug.service.warehouse.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.warehouse.Warehouse;
import com.example.drug.mapper.WarehouseMapper;
import com.example.drug.service.warehouse.WarehouseService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public Result list(String warehouseName, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<Warehouse> list = warehouseMapper.selectByCondition(warehouseName, status);
        PageInfo<Warehouse> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result getById(String warehouseId) {
        return Result.success(warehouseMapper.selectById(warehouseId));
    }

    @Override
    public Result add(Warehouse warehouse) {
        // 生成UUID主键
        warehouse.setWarehouseId(UUIDUtil.getUUID());
        // 生成仓库编码 (W001, W002, ...)
        Integer maxCode = warehouseMapper.getMaxWarehouseCode();
        int nextCode = (maxCode == null ? 0 : maxCode) + 1;
        warehouse.setWarehouseCode(String.format("W%03d", nextCode));
        warehouse.setStatus(1);
        int row = warehouseMapper.insert(warehouse);
        return row > 0 ? Result.success("新增仓库成功") : Result.fail("新增仓库失败");
    }

    @Override
    public Result update(Warehouse warehouse) {
        int row = warehouseMapper.update(warehouse);
        return row > 0 ? Result.success("修改仓库成功") : Result.fail("修改仓库失败");
    }

    @Override
    public Result delete(String warehouseId) {
        int row = warehouseMapper.deleteById(warehouseId);
        return row > 0 ? Result.success("删除仓库成功") : Result.fail("删除仓库失败");
    }
}
