package com.example.drug.entity.sales;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 会员信息实体 (对应 member_info 表)
 */
@Data
@TableName("member_info")
public class MemberInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 会员ID
    @TableId
    private String memberId;
    // 会员卡号
    private String cardNo;
    // 姓名
    private String name;
    // 联系电话
    private String phone;
    // 积分
    private Integer points;
    // 创建时间
    private Date createTime;
}
