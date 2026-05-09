package com.example.drug.service.warehouse.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.warehouse.WarehouseLocation;
import com.example.drug.mapper.WarehouseLocationMapper;
import com.example.drug.service.warehouse.WarehouseLocationService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseLocationServiceImpl implements WarehouseLocationService {

    @Autowired
    private WarehouseLocationMapper locationMapper;

    @Override
    public Result list(String warehouseId, String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<WarehouseLocation> list = locationMapper.selectByCondition(warehouseId, status);
        PageInfo<WarehouseLocation> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result getById(String locId) {
        return Result.success(locationMapper.selectById(locId));
    }

    @Override
    public Result add(WarehouseLocation location) {
        location.setLocId(UUIDUtil.getUUID());
        location.setStatus("空闲");
        // 生成库位编码 (货架号-层号，如 A01-01)
        String locCode = location.getShelf() + "-" + location.getLevel();
        location.setLocCode(locCode);
        int row = locationMapper.insert(location);
        return row > 0 ? Result.success("新增库位成功") : Result.fail("新增库位失败");
    }

    @Override
    public Result update(WarehouseLocation location) {
        // 更新库位编码
        String locCode = location.getShelf() + "-" + location.getLevel();
        location.setLocCode(locCode);
        int row = locationMapper.update(location);
        return row > 0 ? Result.success("修改库位成功") : Result.fail("修改库位失败");
    }

    @Override
    public Result bindInventory(String locId, String status) {
        int row = locationMapper.updateStatus(locId, status);
        return row > 0 ? Result.success("绑定成功") : Result.fail("绑定失败");
    }
}
