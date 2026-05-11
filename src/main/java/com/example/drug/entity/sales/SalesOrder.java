package com.example.drug.entity.sales;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售订单实体 (对应 sales_order 表)
 */
@Data
@TableName("sales_order")
public class SalesOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    // 销售单号
    @TableId
    private String orderId;
    // 会员ID
    private String memberId;
    // 收银员ID
    private String cashierId;
    // 总数量
    private Integer totalNum;
    // 应收金额
    private BigDecimal totalAmount;
    // 优惠
    private BigDecimal discount;
    // 实收金额
    private BigDecimal payAmount;
    // 支付方式：现金/微信/支付宝/医保/银行卡
    private String payType;
    // 订单时间
    private Date orderTime;
    // 状态：已完成/已退货/已取消
    private String status;
    
    // ========== 扩展字段（关联查询）- 不存在于数据库表中 ==========
    // 收银员姓名
    @TableField(exist = false)
    private String cashierName;
    // 会员姓名
    @TableField(exist = false)
    private String memberName;
    // 会员卡号
    @TableField(exist = false)
    private String cardNo;
}
