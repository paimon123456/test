package com.example.drug.service.warehouse.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.TransferDTO;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.entity.warehouse.WarehouseTransfer;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.mapper.WarehouseTransferMapper;
import com.example.drug.service.warehouse.WarehouseTransferService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class WarehouseTransferServiceImpl implements WarehouseTransferService {

    @Autowired
    private WarehouseTransferMapper transferMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Result list(String srcWareId, String destWareId, String status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<WarehouseTransfer> list = transferMapper.selectByCondition(srcWareId, destWareId, status);
        PageInfo<WarehouseTransfer> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result getById(String transferId) {
        return Result.success(transferMapper.selectById(transferId));
    }

    @Override
    public Result apply(TransferDTO dto) {
        WarehouseTransfer transfer = new WarehouseTransfer();
        BeanUtils.copyProperties(dto, transfer);
        transfer.setTransferId(UUIDUtil.getUUID());
        transfer.setTransferNo("YK" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        transfer.setStatus("申请中");
        int row = transferMapper.insert(transfer);
        return row > 0 ? Result.success("移库申请提交成功") : Result.fail("移库申请提交失败");
    }

    @Override
    public Result audit(String transferId, String status, String auditorId) {
        int row = transferMapper.updateStatus(transferId, status);
        return row > 0 ? Result.success("审核成功") : Result.fail("审核失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result finish(String transferId) {
        // 1. 查询移库记录
        WarehouseTransfer transfer = transferMapper.selectById(transferId);
        if (transfer == null) {
            return Result.fail("移库记录不存在");
        }
        if (!"已审核".equals(transfer.getStatus())) {
            return Result.fail("只有已审核的移库单才能执行完成操作");
        }

        // 2. 更新库存表中的仓库ID和库位信息
        Inventory inventory = inventoryMapper.selectById(transfer.getInventoryId());
        if (inventory != null) {
            inventory.setWarehouseId(transfer.getDestWareId());
            // 这里可以根据需要进一步细化到具体库位，如果移库单里有目标库位字段的话
            // 暂时先更新仓库ID
            inventoryMapper.updateById(inventory);
        } else {
            return Result.fail("关联的库存记录不存在");
        }

        // 3. 更新移库单状态为已完成
        int row = transferMapper.updateStatus(transferId, "已完成");
        return row > 0 ? Result.success("移库完成，库存信息已更新") : Result.fail("移库完成失败");
    }
}
