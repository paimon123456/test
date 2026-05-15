package com.example.drug.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 供应商档案DTO
 */
@Data
public class SupplierDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String supplierId;
    private String supplierCode;
    private String supplierName;
    private String contact;
    private String phone;
    private String email;
    private String address;
    private String businessLicense;
    private Date licenseExpiryDate;
    private String gspCertNo;
    private Date gspExpiryDate;
    private String pharmaLicense;
    private Date pharmaExpiryDate;
    private String qualificationStatus;
    private String cooperationStatus;
    private String creditLevel;
    private String remark;
    private Integer status;
}
