package com.example.drug.service.warehouse;

import com.example.drug.dto.TransferDTO;
import com.example.drug.util.Result;

public interface WarehouseTransferService {
    // 分页条件查询移库记录
    Result list(String srcWareId, String destWareId, String status, Integer pageNum, Integer pageSize);
    // 根据ID查询
    Result getById(String transferId);
    // 新增移库申请
    Result apply(TransferDTO dto);
    // 审核移库申请
    Result audit(String transferId, String status, String auditorId);
    // 完成移库
    Result finish(String transferId);
}
