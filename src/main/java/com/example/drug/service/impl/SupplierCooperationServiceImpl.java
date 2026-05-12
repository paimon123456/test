package com.example.drug.service.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.CooperationRecordDTO;
import com.example.drug.entity.supplier.SupplierCooperation;
import com.example.drug.mapper.SupplierCooperationMapper;
import com.example.drug.service.supplier.SupplierCooperationService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 供应商合作记录服务实现类
 */
@Service
public class SupplierCooperationServiceImpl implements SupplierCooperationService {
    
    @Autowired
    private SupplierCooperationMapper cooperationMapper;
    
    /**
     * 添加合作记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addCooperationRecord(CooperationRecordDTO dto) {
        try {
            SupplierCooperation record = new SupplierCooperation();
            record.setRecordId(UUIDUtil.getUUID());
            record.setSupplierId(dto.getSupplierId());
            record.setRecordType(dto.getRecordType());
            record.setContent(dto.getContent());
            record.setAmount(dto.getAmount());
            record.setOperatorId(dto.getOperatorId());
            record.setCreateTime(new Date());
            
            cooperationMapper.insert(record);
            return Result.success("合作记录添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("添加合作记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询合作记录列表
     */
    @Override
    public Result list(String supplierId, String recordType, String startDate, String endDate,
                       Integer pageNum, Integer pageSize) {
        List<SupplierCooperation> records = cooperationMapper.selectCooperationList(
                supplierId, recordType, startDate, endDate);
        
        // 分页处理
        int total = records.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SupplierCooperation> pageRecords = fromIndex < total ? 
                records.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 查询合作记录详情
     */
    @Override
    public Result getById(String recordId) {
        SupplierCooperation record = cooperationMapper.selectCooperationDetail(recordId);
        if (record == null) {
            return Result.fail("合作记录不存在");
        }
        return Result.success(record);
    }
    
    /**
     * 删除合作记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(String recordId) {
        try {
            cooperationMapper.deleteById(recordId);
            return Result.success("合作记录删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除合作记录失败：" + e.getMessage());
        }
    }
}
