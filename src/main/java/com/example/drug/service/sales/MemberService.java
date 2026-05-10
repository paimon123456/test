package com.example.drug.service.sales;

import com.example.drug.util.Result;

/**
 * 会员服务接口
 */
public interface MemberService {
    
    /**
     * 新增会员
     */
    Result addMember(String cardNo, String name, String phone);
    
    /**
     * 根据会员卡号查询会员
     */
    Result getByCardNo(String cardNo);
    
    /**
     * 查询会员列表
     */
    Result list(String name, String phone, Integer pageNum, Integer pageSize);
    
    /**
     * 更新会员积分
     */
    Result updatePoints(String memberId, Integer points);
    
    /**
     * 使用积分抵扣
     */
    Result usePoints(String memberId, Integer points);
}
