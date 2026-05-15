package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.dto.SalesOrderDTO;
import com.example.drug.entity.Drug;
import com.example.drug.entity.inventory.Inventory;
import com.example.drug.entity.sales.MemberInfo;
import com.example.drug.entity.sales.SalesItem;
import com.example.drug.entity.sales.SalesOrder;
import com.example.drug.mapper.DrugMapper;
import com.example.drug.mapper.InventoryMapper;
import com.example.drug.mapper.MemberInfoMapper;
import com.example.drug.mapper.SalesItemMapper;
import com.example.drug.mapper.SalesOrderMapper;
import com.example.drug.service.inventory.InventoryLogService;
import com.example.drug.service.sales.SalesOrderService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 销售订单服务实现类
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService {
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @Autowired
    private SalesItemMapper salesItemMapper;
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private DrugMapper drugMapper;
    
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    
    @Autowired
    private InventoryLogService inventoryLogService;
    
    /**
     * 前台销售开单
     * 核心业务逻辑：验证库存 -> 扣减库存 -> 创建订单 -> 创建明细 -> 更新会员积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createSalesOrder(SalesOrderDTO dto) {
        try {
            if (dto.getItems() == null || dto.getItems().isEmpty()) {
                return Result.fail("销售明细不能为空");
            }
            
            // 1. 验证库存并计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalNum = 0;
            List<SalesItem> salesItems = new ArrayList<>();
            
            for (SalesOrderDTO.SalesItemDTO itemDto : dto.getItems()) {
                // 1.1 验证药品是否存在
                Drug drug = drugMapper.selectById(itemDto.getDrugId());
                if (drug == null) {
                    return Result.fail("药品ID[" + itemDto.getDrugId() + "]不存在");
                }
                
                // 1.2 查询可用库存（按效期优先原则）
                LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Inventory::getDrugId, itemDto.getDrugId())
                       .in(Inventory::getStatus, "正常", "近效期")
                       .gt(Inventory::getStockNum, 0)
                       .orderByAsc(Inventory::getExpiryDate); // 效期优先
                
                List<Inventory> inventories = inventoryMapper.selectList(wrapper);
                
                if (inventories.isEmpty()) {
                    return Result.fail("药品[" + drug.getDrugName() + "]库存不足");
                }
                
                // 1.3 检查库存总量是否足够
                int availableStock = inventories.stream()
                        .mapToInt(Inventory::getStockNum)
                        .sum();
                
                if (availableStock < itemDto.getSaleNum()) {
                    return Result.fail("药品[" + drug.getDrugName() + "]库存不足，当前库存：" + availableStock);
                }
                
                // 1.4 扣减库存（先进先出、近效期先出）
                int remainingQty = itemDto.getSaleNum();
                for (Inventory inventory : inventories) {
                    if (remainingQty <= 0) break;
                    
                    int deductQty = Math.min(remainingQty, inventory.getStockNum());
                    Integer beforeStock = inventory.getStockNum();
                    inventory.setStockNum(inventory.getStockNum() - deductQty);
                    Integer afterStock = inventory.getStockNum();
                    
                    // 更新库存状态
                    updateInventoryStatus(inventory);
                    
                    inventoryMapper.updateById(inventory);
                    
                    // 记录库存变动日志
                    inventoryLogService.logInventoryChange(
                        inventory.getInventoryId(),
                        inventory.getDrugId(),
                        inventory.getBatchNo(),
                        "销售出库",
                        -deductQty,
                        beforeStock,
                        afterStock,
                        "", // 稍后更新为订单ID
                        dto.getCashierId(),
                        "销售出库"
                    );
                    
                    remainingQty -= deductQty;
                }
                
                // 1.5 创建销售明细
                SalesItem salesItem = new SalesItem();
                salesItem.setItemId(UUIDUtil.getUUID());
                salesItem.setOrderId(""); // 稍后更新
                salesItem.setDrugId(itemDto.getDrugId());
                salesItem.setBatchNo(inventories.get(0).getBatchNo()); // 使用最早效期的批号
                salesItem.setSaleNum(itemDto.getSaleNum());
                salesItem.setSalePrice(itemDto.getSalePrice());
                salesItems.add(salesItem);
                
                // 1.6 累计金额和数量
                BigDecimal itemTotal = itemDto.getSalePrice().multiply(new BigDecimal(itemDto.getSaleNum()));
                totalAmount = totalAmount.add(itemTotal);
                totalNum += itemDto.getSaleNum();
            }
            
            // 2. 处理会员积分和折扣
            String memberId = dto.getMemberId();
            BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
            Integer pointsUsed = dto.getPointsUsed() != null ? dto.getPointsUsed() : 0;
            
            // 验证折扣金额不能超过总金额
            if (discount.compareTo(totalAmount) > 0) {
                return Result.fail("优惠金额不能超过订单总金额");
            }
            
            // 如果使用了会员积分，计算积分抵扣金额（假设100积分=1元）
            BigDecimal pointsDiscount = BigDecimal.ZERO;
            if (memberId != null && !memberId.isEmpty() && pointsUsed > 0) {
                MemberInfo member = memberInfoMapper.selectById(memberId);
                if (member != null) {
                    // 验证积分是否足够
                    if (member.getPoints() < pointsUsed) {
                        return Result.fail("会员积分不足，当前积分：" + member.getPoints());
                    }
                    
                    // 计算积分抵扣金额（100积分=1元）
                    pointsDiscount = new BigDecimal(pointsUsed).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    
                    // 验证总优惠不能超过订单金额
                    if (discount.add(pointsDiscount).compareTo(totalAmount) > 0) {
                        return Result.fail("优惠总额不能超过订单总金额");
                    }
                    
                    // 扣除会员积分
                    member.setPoints(member.getPoints() - pointsUsed);
                    memberInfoMapper.updateById(member);
                }
            }
            
            // 3. 计算实收金额
            BigDecimal payAmount = totalAmount.subtract(discount).subtract(pointsDiscount);
            if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                payAmount = BigDecimal.ZERO;
            }
            
            // 4. 创建销售订单
            String orderId = UUIDUtil.getUUID();
            SalesOrder order = new SalesOrder();
            order.setOrderId(orderId);
            order.setMemberId(memberId);
            order.setCashierId(dto.getCashierId());
            order.setTotalNum(totalNum);
            order.setTotalAmount(totalAmount);
            order.setDiscount(discount);
            order.setPayAmount(payAmount);
            order.setPayType(dto.getPayType());
            order.setOrderTime(new Date());
            order.setStatus("已完成");
            
            salesOrderMapper.insert(order);
            
            // 5.1 如果是会员订单，增加会员积分（每消费1元积1分）
            if (memberId != null && !memberId.isEmpty()) {
                MemberInfo member = memberInfoMapper.selectById(memberId);
                if (member != null) {
                    // 按实收金额计算积分
                    int earnedPoints = payAmount.intValue();
                    member.setPoints(member.getPoints() + earnedPoints);
                    memberInfoMapper.updateById(member);
                }
            }
            
            // 6. 保存销售明细
            for (SalesItem item : salesItems) {
                item.setOrderId(orderId);
                salesItemMapper.insert(item);
            }
            
            // 6. 更新库存变动日志中的订单ID（简化处理，实际应记录每条日志的orderId）
            // 由于日志已经记录，这里暂不更新，可以在日志中通过时间范围查询关联
            
            // 7. 返回结果
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("orderId", orderId);
            resultData.put("totalAmount", totalAmount);
            resultData.put("payAmount", payAmount);
            resultData.put("totalNum", totalNum);
            
            return Result.success(resultData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("开单失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询销售订单列表
     */
    @Override
    public Result list(String orderId, String memberId, String cashierId, String status,
                       String startDate, String endDate, Integer pageNum, Integer pageSize) {
        List<SalesOrder> orders = salesOrderMapper.selectOrderList(
                orderId, memberId, cashierId, status, startDate, endDate);
        
        // 分页处理（简化版，实际应使用PageHelper）
        int total = orders.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SalesOrder> pageRecords = fromIndex < total ? 
                orders.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 根据订单ID查询详情（包含明细）
     */
    @Override
    public Result getById(String orderId) {
        // 查询订单主表
        SalesOrder order = salesOrderMapper.selectOrderDetail(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        
        // 查询订单明细
        List<SalesItem> items = salesItemMapper.selectItemsByOrderId(orderId);
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("order", order);
        resultData.put("items", items);
        
        return Result.success(resultData);
    }
    
    /**
     * 销售数据统计（按日期）
     */
    @Override
    public Result statByDate(String startDate, String endDate) {
        List<Map<String, Object>> stats = salesOrderMapper.statSalesByDate(startDate, endDate);
        return Result.success(stats);
    }
    
    /**
     * 销售数据统计（按药品）
     */
    @Override
    public Result statByDrug(String startDate, String endDate) {
        List<Map<String, Object>> stats = salesOrderMapper.statSalesByDrug(startDate, endDate);
        return Result.success(stats);
    }
    
    /**
     * 销售总额统计
     */
    @Override
    public Result statTotalAmount(String startDate, String endDate) {
        BigDecimal totalAmount = salesOrderMapper.statTotalAmount(startDate, endDate);
        return Result.success(totalAmount != null ? totalAmount : BigDecimal.ZERO);
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
