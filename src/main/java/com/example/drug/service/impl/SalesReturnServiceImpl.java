package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.SalesReturnDTO;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.entity.sales.MemberInfo;
import com.example.drug.entity.sales.SalesItem;
import com.example.drug.entity.sales.SalesOrder;
import com.example.drug.entity.sales.SalesReturn;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.mapper.MemberInfoMapper;
import com.example.drug.mapper.SalesItemMapper;
import com.example.drug.mapper.SalesOrderMapper;
import com.example.drug.mapper.SalesReturnMapper;
import com.example.drug.service.sales.SalesReturnService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 销售退货服务实现类
 */
@Service
public class SalesReturnServiceImpl implements SalesReturnService {
    
    @Autowired
    private SalesReturnMapper salesReturnMapper;
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @Autowired
    private SalesItemMapper salesItemMapper;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    
    /**
     * 创建退货申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createReturn(SalesReturnDTO dto) {
        try {
            // 1. 验证原订单是否存在
            SalesOrder originalOrder = salesOrderMapper.selectById(dto.getOriginalOrderId());
            if (originalOrder == null) {
                return Result.fail("原订单不存在");
            }
            
            // 2. 检查订单状态
            if (!"已完成".equals(originalOrder.getStatus())) {
                return Result.fail("订单状态不是已完成，无法退货");
            }
            
            // 3. 遍历退货明细
            for (SalesReturnDTO.ReturnItemDTO itemDto : dto.getItems()) {
                // 3.1 验证该药品是否在订单中
                LambdaQueryWrapper<SalesItem> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SalesItem::getOrderId, dto.getOriginalOrderId())
                       .eq(SalesItem::getDrugId, itemDto.getDrugId())
                       .eq(SalesItem::getBatchNo, itemDto.getBatchNo());
                
                SalesItem orderItem = salesItemMapper.selectOne(wrapper);
                if (orderItem == null) {
                    return Result.fail("药品不在原订单中或批号不匹配");
                }
                
                // 3.2 验证退货数量
                if (itemDto.getReturnNum() > orderItem.getSaleNum()) {
                    return Result.fail("退货数量超过购买数量");
                }
                
                // 3.3 计算退款金额
                BigDecimal refundAmount = orderItem.getSalePrice().multiply(new BigDecimal(itemDto.getReturnNum()));
                
                // 4. 创建退货单
                String returnId = UUIDUtil.getUUID();
                SalesReturn salesReturn = new SalesReturn();
                salesReturn.setReturnId(returnId);
                salesReturn.setOriginalOrderId(dto.getOriginalOrderId());
                salesReturn.setDrugId(itemDto.getDrugId());
                salesReturn.setBatchNo(itemDto.getBatchNo());
                salesReturn.setReturnNum(itemDto.getReturnNum());
                salesReturn.setReturnReason(dto.getReturnReason());
                salesReturn.setRefundAmount(refundAmount);
                salesReturn.setStatus("申请中");
                salesReturn.setOperatorId(dto.getOperatorId());
                salesReturn.setCreateTime(new Date());
                
                salesReturnMapper.insert(salesReturn);
            }
            
            return Result.success("退货申请提交成功，等待审核");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("退货申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 审核退货申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result auditReturn(String returnId, String auditorId, Boolean approved, String remark) {
        try {
            SalesReturn salesReturn = salesReturnMapper.selectById(returnId);
            if (salesReturn == null) {
                return Result.fail("退货单不存在");
            }
            
            if (!"申请中".equals(salesReturn.getStatus())) {
                return Result.fail("退货单状态不是申请中");
            }
            
            if (approved) {
                // 审核通过
                salesReturn.setStatus("已完成");
                salesReturn.setAuditorId(auditorId);
                salesReturn.setAuditTime(new Date());
                salesReturnMapper.updateById(salesReturn);
                
                // 库存回补
                LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Inventory::getDrugId, salesReturn.getDrugId())
                       .eq(Inventory::getBatchNo, salesReturn.getBatchNo());
                
                Inventory inventory = inventoryMapper.selectOne(wrapper);
                if (inventory != null) {
                    inventory.setStockNum(inventory.getStockNum() + salesReturn.getReturnNum());
                    updateInventoryStatus(inventory);
                    inventoryMapper.updateById(inventory);
                }
                
                // 更新原订单状态
                SalesOrder order = salesOrderMapper.selectById(salesReturn.getOriginalOrderId());
                if (order != null) {
                    order.setStatus("已退货");
                    salesOrderMapper.updateById(order);
                    
                    // 处理会员积分：退还已用积分、扣除已获积分
                    if (order.getMemberId() != null && !order.getMemberId().isEmpty()) {
                        MemberInfo member = memberInfoMapper.selectById(order.getMemberId());
                        if (member != null && order.getTotalAmount() != null 
                                && order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                            // 计算退货比例 = 退款金额 / 订单应收金额
                            BigDecimal refundRatio = salesReturn.getRefundAmount()
                                    .divide(order.getTotalAmount(), 4, BigDecimal.ROUND_HALF_UP);
                            
                            // 1. 扣除已获积分（购买时按实收金额每1元积1分）
                            int earnedPoints = order.getPayAmount() != null ? order.getPayAmount().intValue() : 0;
                            int earnedPointsToDeduct = new BigDecimal(earnedPoints)
                                    .multiply(refundRatio)
                                    .setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .intValue();
                            
                            // 2. 退还已用积分（从discount推导：discount元 = discount*100积分）
                            int usedPoints = order.getDiscount() != null 
                                    ? order.getDiscount().multiply(new BigDecimal(100)).intValue() : 0;
                            int usedPointsToRefund = new BigDecimal(usedPoints)
                                    .multiply(refundRatio)
                                    .setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .intValue();
                            
                            // 积分变动 = 退还已用积分 - 扣除已获积分
                            int pointsChange = usedPointsToRefund - earnedPointsToDeduct;
                            member.setPoints(member.getPoints() + pointsChange);
                            // 防止积分变为负数
                            if (member.getPoints() < 0) {
                                member.setPoints(0);
                            }
                            memberInfoMapper.updateById(member);
                        }
                    }
                }
                
                return Result.success("审核通过，库存已回补");
            } else {
                // 审核驳回
                salesReturn.setStatus("已驳回");
                salesReturn.setAuditorId(auditorId);
                salesReturn.setAuditTime(new Date());
                salesReturnMapper.updateById(salesReturn);
                
                return Result.success("审核驳回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询退货单列表
     */
    @Override
    public Result list(String originalOrderId, String drugName, String status,
                       String startDate, String endDate, Integer pageNum, Integer pageSize) {
        List<SalesReturn> returns = salesReturnMapper.selectReturnList(
                originalOrderId, drugName, status, startDate, endDate);
        
        // 分页处理
        int total = returns.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SalesReturn> pageRecords = fromIndex < total ? 
                returns.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 根据退货单ID查询详情
     */
    @Override
    public Result getById(String returnId) {
        SalesReturn salesReturn = salesReturnMapper.selectReturnDetail(returnId);
        if (salesReturn == null) {
            return Result.fail("退货单不存在");
        }
        return Result.success(salesReturn);
    }
    
    /**
     * 更新库存状态
     */
    private void updateInventoryStatus(Inventory inventory) {
        if (inventory.getExpiryDate() == null) {
            inventory.setStatus("正常");
            return;
        }
        
        Date now = new Date();
        long diffMillis = inventory.getExpiryDate().getTime() - now.getTime();
        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        
        if (diffDays < 0) {
            inventory.setStatus("过期");
        } else if (diffDays <= 90) {
            inventory.setStatus("近效期");
        } else {
            inventory.setStatus("正常");
        }
    }
}
