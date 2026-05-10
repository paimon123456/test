package com.example.drug.controller;

import com.example.drug.dto.SalesOrderDTO;
import com.example.drug.dto.SalesReturnDTO;
import com.example.drug.service.sales.MemberService;
import com.example.drug.service.sales.SalesOrderService;
import com.example.drug.service.sales.SalesReturnService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 销售管理控制器
 */
@RestController
@RequestMapping("/sales")
public class SalesManagementController {
    
    @Autowired
    private SalesOrderService salesOrderService;
    
    @Autowired
    private SalesReturnService salesReturnService;
    
    @Autowired
    private MemberService memberService;
    
    // ==================== 销售订单管理 ====================
    
    /**
     * 前台销售开单
     */
    @PostMapping("/order/create")
    public Result createSalesOrder(@RequestBody SalesOrderDTO dto) {
        return salesOrderService.createSalesOrder(dto);
    }
    
    /**
     * 查询销售订单列表
     */
    @GetMapping("/order/list")
    public Result listOrders(@RequestParam(required = false) String orderId,
                             @RequestParam(required = false) String memberId,
                             @RequestParam(required = false) String cashierId,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return salesOrderService.list(orderId, memberId, cashierId, status, 
                                      startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 根据订单ID查询详情（包含明细）
     */
    @GetMapping("/order/{orderId}")
    public Result getOrderById(@PathVariable String orderId) {
        return salesOrderService.getById(orderId);
    }
    
    /**
     * 销售数据统计（按日期）
     */
    @GetMapping("/order/stat/date")
    public Result statByDate(@RequestParam String startDate,
                             @RequestParam String endDate) {
        return salesOrderService.statByDate(startDate, endDate);
    }
    
    /**
     * 销售数据统计（按药品）
     */
    @GetMapping("/order/stat/drug")
    public Result statByDrug(@RequestParam String startDate,
                             @RequestParam String endDate) {
        return salesOrderService.statByDrug(startDate, endDate);
    }
    
    /**
     * 销售总额统计
     */
    @GetMapping("/order/stat/total")
    public Result statTotalAmount(@RequestParam String startDate,
                                  @RequestParam String endDate) {
        return salesOrderService.statTotalAmount(startDate, endDate);
    }
    
    // ==================== 销售退货管理 ====================
    
    /**
     * 创建退货申请
     */
    @PostMapping("/return/create")
    public Result createReturn(@RequestBody SalesReturnDTO dto) {
        return salesReturnService.createReturn(dto);
    }
    
    /**
     * 审核退货申请
     */
    @PostMapping("/return/audit")
    public Result auditReturn(@RequestParam String returnId,
                              @RequestParam String auditorId,
                              @RequestParam Boolean approved,
                              @RequestParam(required = false) String remark) {
        return salesReturnService.auditReturn(returnId, auditorId, approved, remark);
    }
    
    /**
     * 查询退货单列表
     */
    @GetMapping("/return/list")
    public Result listReturns(@RequestParam(required = false) String originalOrderId,
                              @RequestParam(required = false) String drugName,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return salesReturnService.list(originalOrderId, drugName, status,
                                       startDate, endDate, pageNum, pageSize);
    }
    
    /**
     * 根据退货单ID查询详情
     */
    @GetMapping("/return/{returnId}")
    public Result getReturnById(@PathVariable String returnId) {
        return salesReturnService.getById(returnId);
    }
    
    // ==================== 会员管理 ====================
    
    /**
     * 新增会员
     */
    @PostMapping("/member/add")
    public Result addMember(@RequestParam String cardNo,
                            @RequestParam String name,
                            @RequestParam String phone) {
        return memberService.addMember(cardNo, name, phone);
    }
    
    /**
     * 根据会员卡号查询会员
     */
    @GetMapping("/member/card/{cardNo}")
    public Result getMemberByCardNo(@PathVariable String cardNo) {
        return memberService.getByCardNo(cardNo);
    }
    
    /**
     * 查询会员列表
     */
    @GetMapping("/member/list")
    public Result listMembers(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String phone,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return memberService.list(name, phone, pageNum, pageSize);
    }
    
    /**
     * 更新会员积分
     */
    @PostMapping("/member/update-points")
    public Result updatePoints(@RequestParam String memberId,
                               @RequestParam Integer points) {
        return memberService.updatePoints(memberId, points);
    }
}
