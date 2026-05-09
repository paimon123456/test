package com.example.drug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.drug.entity.Drug;
import java.util.List;

public interface DrugService extends IService<Drug> {
    void resetIdSequence();
    List<Drug> getExpiringDrugs(Integer days);
}
