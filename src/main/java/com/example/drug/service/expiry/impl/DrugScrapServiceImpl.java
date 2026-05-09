package com.example.drug.service.expiry.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.ScrapDTO;
import com.example.drug.entity.expiry.DrugScrap;
import com.example.drug.mapper.DrugScrapMapper;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.service.expiry.DrugScrapService;
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
public class DrugScrapServiceImpl implements DrugScrapService {

    @Autowired
    private DrugScrapMapper scrapMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Result list(String status, String drugId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        List<DrugScrap> list = scrapMapper.selectByCondition(status, drugId);
        PageInfo<DrugScrap> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @Override
    public Result getById(String scrapId) {
        return Result.success(scrapMapper.selectById(scrapId));
    }

    @Override
    public Result apply(ScrapDTO dto) {
        DrugScrap scrap = new DrugScrap();
        BeanUtils.copyProperties(dto, scrap);
        scrap.setScrapId(UUIDUtil.getUUID());
        scrap.setScrapNo("BF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        scrap.setStatus("申请中");
        int row = scrapMapper.insert(scrap);
        return row > 0 ? Result.success("报废申请提交成功") : Result.fail("报废申请提交失败");
    }

    @Override
    public Result audit(String scrapId, String status, String auditorId) {
        int row = scrapMapper.updateStatus(scrapId, status);
        return row > 0 ? Result.success("审核成功") : Result.fail("审核失败");
    }

    @Override
    public Result approve(String scrapId, String status, String approverId) {
        int row = scrapMapper.updateStatus(scrapId, status);
        return row > 0 ? Result.success("审批成功") : Result.fail("审批失败");
    }

    @Override
    @Transactional
    public Result execute(String scrapId, String outboundId) {
        // 1. 查询报废记录获取库存ID和报废数量
        DrugScrap scrap = scrapMapper.selectById(scrapId);
        if (scrap == null) {
            return Result.fail("报废记录不存在");
        }

        // 2. 减少库存数量
        int reduced = inventoryMapper.reduceStock(scrap.getInventoryId(), scrap.getScrapNum());
        if (reduced == 0) {
            return Result.fail("库存不足，无法执行报废");
        }

        // 3. 更新报废状态为已执行
        int row = scrapMapper.updateStatus(scrapId, "已执行");
        return row > 0 ? Result.success("报废执行成功，库存已扣减") : Result.fail("报废执行失败");
    }
}
