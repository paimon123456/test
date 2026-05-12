package com.example.drug.controller;

import com.example.drug.entity.price.BatchPriceAdjust;
import com.example.drug.entity.price.DrugPrice;
import com.example.drug.entity.price.PriceAdjust;
import com.example.drug.service.price.PriceService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @GetMapping("/list")
    public Result getDrugPriceList(@RequestParam(required = false) String drugId,
                                    @RequestParam(required = false) String keyword) {
        return priceService.getDrugPriceList(drugId, keyword);
    }

    @GetMapping("/get")
    public Result getDrugPrice(@RequestParam String drugId) {
        return priceService.getDrugPrice(drugId);
    }

    @PostMapping("/save")
    public Result saveDrugPrice(@RequestBody DrugPrice drugPrice) {
        return priceService.saveDrugPrice(drugPrice);
    }

    @PostMapping("/update")
    public Result updateDrugPrice(@RequestBody DrugPrice drugPrice) {
        return priceService.updateDrugPrice(drugPrice);
    }

    @GetMapping("/history")
    public Result getPriceHistory(@RequestParam(required = false) String drugId,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(required = false) String priceType) {
        return priceService.getPriceHistory(drugId, startDate, endDate, priceType);
    }

    @PostMapping("/adjust")
    public Result adjustPrice(@RequestBody PriceAdjust priceAdjust) {
        return priceService.adjustPrice(priceAdjust);
    }

    @PostMapping("/batchAdjust")
    public Result batchAdjustPrice(@RequestBody BatchPriceAdjust batchAdjust) {
        return priceService.batchAdjustPrice(batchAdjust);
    }

    @GetMapping("/current")
    public Result getCurrentPrice(@RequestParam String drugId) {
        return priceService.getCurrentPrice(drugId);
    }

    @PostMapping("/batchSave")
    public Result batchSavePrice(@RequestBody List<DrugPrice> priceList) {
        try {
            for (DrugPrice price : priceList) {
                priceService.updateDrugPrice(price);
            }
            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("批量保存价格失败：" + e.getMessage());
        }
    }
}
