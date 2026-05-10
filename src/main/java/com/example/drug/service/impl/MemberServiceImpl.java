package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.sales.MemberInfo;
import com.example.drug.mapper.MemberInfoMapper;
import com.example.drug.service.sales.MemberService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 会员服务实现类
 */
@Service
public class MemberServiceImpl implements MemberService {
    
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    
    /**
     * 新增会员
     */
    @Override
    public Result addMember(String cardNo, String name, String phone) {
        try {
            // 检查会员卡号是否已存在
            MemberInfo existing = memberInfoMapper.selectByCardNo(cardNo);
            if (existing != null) {
                return Result.fail("会员卡号[" + cardNo + "]已存在");
            }
            
            MemberInfo member = new MemberInfo();
            member.setMemberId(UUIDUtil.getUUID());
            member.setCardNo(cardNo);
            member.setName(name);
            member.setPhone(phone);
            member.setPoints(0);
            member.setCreateTime(new Date());
            
            memberInfoMapper.insert(member);
            return Result.success("会员注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("注册失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据会员卡号查询会员
     */
    @Override
    public Result getByCardNo(String cardNo) {
        MemberInfo member = memberInfoMapper.selectByCardNo(cardNo);
        if (member == null) {
            return Result.fail("会员不存在");
        }
        return Result.success(member);
    }
    
    /**
     * 查询会员列表
     */
    @Override
    public Result list(String name, String phone, Integer pageNum, Integer pageSize) {
        List<MemberInfo> members = memberInfoMapper.selectMemberList(name, phone);
        
        // 分页处理
        int total = members.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<MemberInfo> pageRecords = fromIndex < total ? 
                members.subList(fromIndex, toIndex) : Collections.emptyList();
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("records", pageRecords);
        
        return Result.success(data);
    }
    
    /**
     * 更新会员积分
     */
    @Override
    public Result updatePoints(String memberId, Integer points) {
        MemberInfo member = memberInfoMapper.selectById(memberId);
        if (member == null) {
            return Result.fail("会员不存在");
        }
        
        member.setPoints(member.getPoints() + points);
        memberInfoMapper.updateById(member);
        
        return Result.success("积分更新成功，当前积分：" + member.getPoints());
    }
    
    /**
     * 使用积分抵扣
     */
    @Override
    public Result usePoints(String memberId, Integer points) {
        MemberInfo member = memberInfoMapper.selectById(memberId);
        if (member == null) {
            return Result.fail("会员不存在");
        }
        
        if (member.getPoints() < points) {
            return Result.fail("积分不足，当前积分：" + member.getPoints());
        }
        
        member.setPoints(member.getPoints() - points);
        memberInfoMapper.updateById(member);
        
        return Result.success("积分抵扣成功，剩余积分：" + member.getPoints());
    }
}
