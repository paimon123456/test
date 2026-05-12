package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.SupplierDTO;
import com.example.drug.entity.supplier.Supplier;
import com.example.drug.mapper.SupplierMapper;
import com.example.drug.service.supplier.SupplierService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl implements SupplierService {
    
    @Autowired
    private SupplierMapper supplierMapper;
    
    /**
     * 新增供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addSupplier(SupplierDTO dto) {
        try {
            // 检查编码唯一性
            Supplier existCode = supplierMapper.selectByCode(dto.getSupplierCode());
            if (existCode != null) {
                return Result.fail("供应商编码已存在");
            }
            
            // 检查名称唯一性
            LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Supplier::getSupplierName, dto.getSupplierName());
            if (supplierMapper.selectCount(wrapper) > 0) {
                return Result.fail("供应商名称已存在");
            }
            
            Supplier supplier = new Supplier();
            supplier.setSupplierId(UUIDUtil.getUUID());
            supplier.setSupplierCode(dto.getSupplierCode());
            supplier.setSupplierName(dto.getSupplierName());
            supplier.setContact(dto.getContact());
            supplier.setPhone(dto.getPhone());
            supplier.setEmail(dto.getEmail());
            supplier.setAddress(dto.getAddress());
            supplier.setBusinessLicense(dto.getBusinessLicense());
            supplier.setLicenseExpiryDate(dto.getLicenseExpiryDate());
            supplier.setGspCertNo(dto.getGspCertNo());
            supplier.setGspExpiryDate(dto.getGspExpiryDate());
            supplier.setPharmaLicense(dto.getPharmaLicense());
            supplier.setPharmaExpiryDate(dto.getPharmaExpiryDate());
            supplier.setQualificationStatus(dto.getQualificationStatus() != null ? dto.getQualificationStatus() : "正常");
            supplier.setCooperationStatus(dto.getCooperationStatus() != null ? dto.getCooperationStatus() : "合作中");
            supplier.setCreditLevel(dto.getCreditLevel() != null ? dto.getCreditLevel() : "B");
            supplier.setRemark(dto.getRemark());
            supplier.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            supplier.setCreateTime(new Date());
            supplier.setUpdateTime(new Date());
            
            supplierMapper.insert(supplier);
            return Result.success("供应商新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("新增供应商失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateSupplier(SupplierDTO dto) {
        try {
            Supplier supplier = supplierMapper.selectById(dto.getSupplierId());
            if (supplier == null) {
                return Result.fail("供应商不存在");
            }
            
            // 检查编码唯一性（排除自身）
            Supplier existCode = supplierMapper.selectByCode(dto.getSupplierCode());
            if (existCode != null && !existCode.getSupplierId().equals(dto.getSupplierId())) {
                return Result.fail("供应商编码已存在");
            }
            
            supplier.setSupplierCode(dto.getSupplierCode());
            supplier.setSupplierName(dto.getSupplierName());
            supplier.setContact(dto.getContact());
            supplier.setPhone(dto.getPhone());
            supplier.setEmail(dto.getEmail());
            supplier.setAddress(dto.getAddress());
            supplier.setBusinessLicense(dto.getBusinessLicense());
            supplier.setLicenseExpiryDate(dto.getLicenseExpiryDate());
            supplier.setGspCertNo(dto.getGspCertNo());
            supplier.setGspExpiryDate(dto.getGspExpiryDate());
            supplier.setPharmaLicense(dto.getPharmaLicense());
            supplier.setPharmaExpiryDate(dto.getPharmaExpiryDate());
            supplier.setQualificationStatus(dto.getQualificationStatus());
            supplier.setCooperationStatus(dto.getCooperationStatus());
            supplier.setCreditLevel(dto.getCreditLevel());
            supplier.setRemark(dto.getRemark());
            if (dto.getStatus() != null) {
                supplier.setStatus(dto.getStatus());
            }
            supplier.setUpdateTime(new Date());
            
            supplierMapper.updateById(supplier);
            return Result.success("供应商修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("修改供应商失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteSupplier(String supplierId) {
        try {
            Supplier supplier = supplierMapper.selectById(supplierId);
            if (supplier == null) {
                return Result.fail("供应商不存在");
            }
            
            // 软删除
            supplier.setStatus(0);
            supplier.setUpdateTime(new Date());
            supplierMapper.updateById(supplier);
            
            return Result.success("供应商删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除供应商失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询供应商列表
     */
    @Override
    public Result list(String supplierName, String qualificationStatus, String cooperationStatus,
                       Integer status, Integer pageNum, Integer pageSize) {
        List<Supplier> suppliers = supplierMapper.selectSupplierList(
                supplierName, qualificationStatus, cooperationStatus, status);
        
        // 分页处理
        int total = suppliers.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Supplier> pageRecords = fromIndex < total ? 
                suppliers.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 查询供应商详情
     */
    @Override
    public Result getById(String supplierId) {
        Supplier supplier = supplierMapper.selectSupplierDetail(supplierId);
        if (supplier == null) {
            return Result.fail("供应商不存在");
        }
        return Result.success(supplier);
    }
    
    /**
     * 根据编码查询
     */
    @Override
    public Result getByCode(String supplierCode) {
        Supplier supplier = supplierMapper.selectByCode(supplierCode);
        if (supplier == null) {
            return Result.fail("供应商不存在");
        }
        return Result.success(supplier);
    }
    
    /**
     * 资质过期提醒
     */
    @Override
    public Result qualificationReminder(Integer days) {
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }
        List<Supplier> suppliers = supplierMapper.selectExpiringSuppliers(days);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", suppliers.size());
        data.put("reminders", suppliers);
        data.put("reminderDays", days);
        
        return Result.success(data);
    }
    
    /**
     * 更新资质状态（定时任务调用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateQualificationStatus() {
        try {
            LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Supplier::getStatus, 1);
            List<Supplier> suppliers = supplierMapper.selectList(wrapper);
            
            Date now = new Date();
            int updated = 0;
            
            for (Supplier supplier : suppliers) {
                boolean needUpdate = false;
                
                // 检查药品经营许可证
                if (supplier.getPharmaExpiryDate() != null) {
                    long diffDays = (supplier.getPharmaExpiryDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    if (diffDays < 0) {
                        supplier.setQualificationStatus("过期");
                        needUpdate = true;
                    } else if (diffDays <= 30) {
                        supplier.setQualificationStatus("近效期");
                        needUpdate = true;
                    }
                }
                
                // 检查GSP证书
                if (supplier.getGspExpiryDate() != null) {
                    long diffDays = (supplier.getGspExpiryDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    if (diffDays < 0) {
                        supplier.setQualificationStatus("过期");
                        needUpdate = true;
                    } else if (diffDays <= 30 && !"过期".equals(supplier.getQualificationStatus())) {
                        supplier.setQualificationStatus("近效期");
                        needUpdate = true;
                    }
                }
                
                if (needUpdate) {
                    supplier.setUpdateTime(now);
                    supplierMapper.updateById(supplier);
                    updated++;
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("total", suppliers.size());
            data.put("updated", updated);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新资质状态失败：" + e.getMessage());
        }
    }
}
