package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.sales.MemberInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员信息Mapper
 */
@Mapper
public interface MemberInfoMapper extends BaseMapper<MemberInfo> {
    
    /**
     * 根据会员卡号查询会员
     */
    MemberInfo selectByCardNo(@Param("cardNo") String cardNo);
    
    /**
     * 查询会员列表
     */
    List<MemberInfo> selectMemberList(@Param("name") String name,
                                      @Param("phone") String phone);
}
