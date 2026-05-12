package com.example.drug.service.price;

import com.example.drug.entity.price.BatchPriceAdjust;
import com.example.drug.entity.price.DrugPrice;
import com.example.drug.entity.price.PriceAdjust;
import com.example.drug.entity.price.PriceHistory;
import com.example.drug.util.Result;

import java.util.List;

public interface PriceService {

    Result getDrugPriceList(String drugId, String keyword);

    Result getDrugPrice(String drugId);

    Result saveDrugPrice(DrugPrice drugPrice);

    Result updateDrugPrice(DrugPrice drugPrice);

    Result getPriceHistory(String drugId, String startDate, String endDate, String priceType);

    Result adjustPrice(PriceAdjust priceAdjust);

    Result batchAdjustPrice(BatchPriceAdjust batchAdjust);

    Result getCurrentPrice(String drugId);
}
