package com.example.drug.service.expiry;

import com.example.drug.dto.ScrapDTO;
import com.example.drug.util.Result;
import org.springframework.transaction.annotation.Transactional;

public interface DrugScrapService {
    // 分页条件查询报废记录
    Result list(String status, String drugId, Integer pageNum, Integer pageSize);
    // 根据ID查询
    Result getById(String scrapId);
    // 新增报废申请
    Result apply(ScrapDTO dto);
    // 审核报废申请
    Result audit(String scrapId, String status, String auditorId);
    // 审批报废申请
    Result approve(String scrapId, String status, String approverId);
    // 执行报废（联动扣减库存）
    @Transactional
    Result execute(String scrapId, String outboundId);
}
