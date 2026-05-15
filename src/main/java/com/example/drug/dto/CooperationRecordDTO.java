package com.example.drug.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合作记录DTO
 */
@Data
public class CooperationRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String supplierId;
    private String recordType;
    private String content;
    private BigDecimal amount;
    private String operatorId;
}
