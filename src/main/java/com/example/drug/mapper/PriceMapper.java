package com.example.drug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.drug.entity.price.DrugPrice;
import com.example.drug.entity.price.PriceHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriceMapper extends BaseMapper<Object> {

    List<DrugPrice> getDrugPriceList(@Param("drugId") String drugId,
                                      @Param("keyword") String keyword);

    DrugPrice getDrugPriceById(@Param("drugId") String drugId);

    int saveDrugPrice(DrugPrice drugPrice);

    int updateDrugPrice(DrugPrice drugPrice);

    List<PriceHistory> getPriceHistory(@Param("drugId") String drugId,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        @Param("priceType") String priceType);

    int savePriceHistory(PriceHistory priceHistory);

    int batchUpdatePrice(@Param("drugIds") List<String> drugIds,
                         @Param("priceType") String priceType,
                         @Param("newPrice") java.math.BigDecimal newPrice,
                         @Param("operator") String operator);

    int batchAdjustPriceByRate(@Param("drugIds") List<String> drugIds,
                                @Param("priceType") String priceType,
                                @Param("adjustRate") java.math.BigDecimal adjustRate,
                                @Param("operator") String operator);
}
