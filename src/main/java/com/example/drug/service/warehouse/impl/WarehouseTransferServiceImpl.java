package com.example.drug.service.warehouse.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.TransferDTO;
import com.example.drug.entity.warehouse.WarehouseTransfer;
import com.example.drug.mapper.WarehouseTransferMapper;
import com.example.drug.service.warehouse.WarehouseTransferService;
import com.example.drug.util.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class WarehouseTransferServiceImpl implements WarehouseTransferService {

    @Autowired
    private WarehouseTransferMapper transferMapper;

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
    public Result finish(String transferId) {
        int row = transferMapper.updateStatus(transferId, "已完成");
        return row > 0 ? Result.success("移库完成") : Result.fail("移库完成失败");
    }
}
