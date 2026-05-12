package com.example.drug.service.impl;

import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.Drug;
import com.example.drug.entity.price.BatchPriceAdjust;
import com.example.drug.entity.price.DrugPrice;
import com.example.drug.entity.price.PriceAdjust;
import com.example.drug.entity.price.PriceHistory;
import com.example.drug.mapper.DrugMapper;
import com.example.drug.mapper.PriceMapper;
import com.example.drug.service.price.PriceService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceMapper priceMapper;

    @Autowired
    private DrugMapper drugMapper;

    @Override
    public Result getDrugPriceList(String drugId, String keyword) {
        try {
            List<DrugPrice> list = priceMapper.getDrugPriceList(drugId, keyword);
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("获取药品价格列表失败：" + e.getMessage());
        }
    }

    @Override
    public Result getDrugPrice(String drugId) {
        try {
            DrugPrice price = priceMapper.getDrugPriceById(drugId);
            if (price == null) {
                Drug drug = drugMapper.selectById(drugId);
                if (drug == null) {
                    return Result.fail("药品不存在");
                }
                price = new DrugPrice();
                price.setDrugId(drugId);
                price.setDrugName(drug.getDrugName());
                price.setSpecification(drug.getSpecification());
                price.setPurchasePrice(drug.getPurchasePrice());
                price.setRetailPrice(drug.getRetailPrice());
            }
            return Result.success(price);
        } catch (Exception e) {
            return Result.fail("获取药品价格失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveDrugPrice(DrugPrice drugPrice) {
        try {
            if (drugPrice.getDrugId() == null || drugPrice.getDrugId().isEmpty()) {
                return Result.fail("药品ID不能为空");
            }

            DrugPrice existing = priceMapper.getDrugPriceById(drugPrice.getDrugId());
            if (existing != null && existing.getPriceId() != null) {
                return Result.fail("该药品价格已存在，请使用更新接口");
            }

            drugPrice.setPriceId(UUIDUtil.getUUID());
            drugPrice.setUpdateTime(new Date());

            priceMapper.saveDrugPrice(drugPrice);

            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("保存药品价格失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDrugPrice(DrugPrice drugPrice) {
        try {
            if (drugPrice.getDrugId() == null || drugPrice.getDrugId().isEmpty()) {
                return Result.fail("药品ID不能为空");
            }

            DrugPrice existing = priceMapper.getDrugPriceById(drugPrice.getDrugId());
            Drug drug = drugMapper.selectById(drugPrice.getDrugId());
            if (drug == null) {
                return Result.fail("药品不存在");
            }

            BigDecimal oldPurchase = existing != null && existing.getPurchasePrice() != null
                    ? existing.getPurchasePrice() : drug.getPurchasePrice();
            BigDecimal oldRetail = existing != null && existing.getRetailPrice() != null
                    ? existing.getRetailPrice() : drug.getRetailPrice();

            if (existing != null && existing.getPriceId() != null) {
                priceMapper.updateDrugPrice(drugPrice);
            } else {
                drugPrice.setPriceId(UUIDUtil.getUUID());
                priceMapper.saveDrugPrice(drugPrice);
            }

            if (drugPrice.getPurchasePrice() != null && !drugPrice.getPurchasePrice().equals(oldPurchase)) {
                savePriceHistory(drugPrice.getDrugId(), "purchase", oldPurchase, drugPrice.getPurchasePrice(),
                        drugPrice.getOperator(), "手动调整");
            }
            if (drugPrice.getRetailPrice() != null && !drugPrice.getRetailPrice().equals(oldRetail)) {
                savePriceHistory(drugPrice.getDrugId(), "retail", oldRetail, drugPrice.getRetailPrice(),
                        drugPrice.getOperator(), "手动调整");
            }

            return Result.success(true);
        } catch (Exception e) {
            return Result.fail("更新药品价格失败：" + e.getMessage());
        }
    }

    @Override
    public Result getPriceHistory(String drugId, String startDate, String endDate, String priceType) {
        try {
            List<PriceHistory> list = priceMapper.getPriceHistory(drugId, startDate, endDate, priceType);
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("获取价格调整历史失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result adjustPrice(PriceAdjust priceAdjust) {
        try {
            if (priceAdjust.getItems() == null || priceAdjust.getItems().isEmpty()) {
                return Result.fail("调价项目不能为空");
            }

            List<Map<String, Object>> results = new ArrayList<>();

            for (PriceAdjust.PriceAdjustItem item : priceAdjust.getItems()) {
                Drug drug = drugMapper.selectById(item.getDrugId());
                if (drug == null) {
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("drugId", item.getDrugId());
                    result.put("success", false);
                    result.put("message", "药品不存在");
                    results.add(result);
                    continue;
                }

                BigDecimal oldPrice = getOldPrice(drug, item.getPriceType());
                BigDecimal newPrice = item.getNewPrice();

                DrugPrice drugPrice = priceMapper.getDrugPriceById(item.getDrugId());
                if (drugPrice == null) {
                    drugPrice = new DrugPrice();
                    drugPrice.setDrugId(item.getDrugId());
                    drugPrice.setPurchasePrice(drug.getPurchasePrice());
                    drugPrice.setRetailPrice(drug.getRetailPrice());
                }

                updatePrice(drugPrice, item.getPriceType(), newPrice);
                priceMapper.updateDrugPrice(drugPrice);

                savePriceHistory(item.getDrugId(), item.getPriceType(), oldPrice, newPrice,
                        priceAdjust.getOperator(), priceAdjust.getReason());

                Map<String, Object> result = new java.util.HashMap<>();
                result.put("drugId", item.getDrugId());
                result.put("drugName", drug.getDrugName());
                result.put("oldPrice", oldPrice);
                result.put("newPrice", newPrice);
                result.put("success", true);
                result.put("message", "调价成功");
                results.add(result);
            }

            return Result.success(results);
        } catch (Exception e) {
            return Result.fail("调价失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchAdjustPrice(BatchPriceAdjust batchAdjust) {
        try {
            if (batchAdjust.getDrugIds() == null || batchAdjust.getDrugIds().isEmpty()) {
                return Result.fail("请选择要调价的药品");
            }

            if (batchAdjust.getAdjustRate() == null && batchAdjust.getItems() == null) {
                return Result.fail("请提供调价幅度或具体价格");
            }

            List<BatchPriceAdjust.Result> results = new ArrayList<>();

            for (String drugId : batchAdjust.getDrugIds()) {
                Drug drug = drugMapper.selectById(drugId);
                if (drug == null) {
                    BatchPriceAdjust.Result result = new BatchPriceAdjust.Result();
                    result.setDrugId(drugId);
                    result.setSuccess(false);
                    result.setMessage("药品不存在");
                    results.add(result);
                    continue;
                }

                DrugPrice drugPrice = priceMapper.getDrugPriceById(drugId);
                if (drugPrice == null) {
                    drugPrice = new DrugPrice();
                    drugPrice.setDrugId(drugId);
                    drugPrice.setPurchasePrice(drug.getPurchasePrice());
                    drugPrice.setRetailPrice(drug.getRetailPrice());
                }

                BigDecimal oldPrice = getOldPrice(drug, batchAdjust.getPriceType());
                BigDecimal newPrice;

                if (batchAdjust.getAdjustRate() != null) {
                    newPrice = oldPrice.multiply(BigDecimal.ONE.add(batchAdjust.getAdjustRate()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                } else {
                    if (batchAdjust.getItems() != null) {
                        for (BatchPriceAdjust.Result item : batchAdjust.getItems()) {
                            if (drugId.equals(item.getDrugId())) {
                                newPrice = item.getNewPrice();
                                break;
                            }
                        }
                        newPrice = oldPrice;
                    } else {
                        newPrice = oldPrice;
                    }
                }

                updatePrice(drugPrice, batchAdjust.getPriceType(), newPrice);
                priceMapper.updateDrugPrice(drugPrice);

                BigDecimal changeRate = oldPrice.compareTo(BigDecimal.ZERO) > 0
                        ? newPrice.subtract(oldPrice).divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal(100))
                        : BigDecimal.ZERO;

                savePriceHistory(drugId, batchAdjust.getPriceType(), oldPrice, newPrice,
                        batchAdjust.getOperator(), batchAdjust.getReason());

                BatchPriceAdjust.Result result = new BatchPriceAdjust.Result();
                result.setDrugId(drugId);
                result.setDrugName(drug.getDrugName());
                result.setOldPrice(oldPrice);
                result.setNewPrice(newPrice);
                result.setChangeRate(changeRate);
                result.setSuccess(true);
                result.setMessage("调价成功");
                results.add(result);
            }

            return Result.success(results);
        } catch (Exception e) {
            return Result.fail("批量调价失败：" + e.getMessage());
        }
    }

    @Override
    public Result getCurrentPrice(String drugId) {
        try {
            Drug drug = drugMapper.selectById(drugId);
            if (drug == null) {
                return Result.fail("药品不存在");
            }

            DrugPrice price = priceMapper.getDrugPriceById(drugId);
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("drugId", drugId);
            result.put("drugName", drug.getDrugName());
            result.put("specification", drug.getSpecification());

            if (price != null && price.getPromoPrice() != null) {
                Date now = new Date();
                if (price.getPromoStart() != null && price.getPromoEnd() != null) {
                    if (now.after(price.getPromoStart()) && now.before(price.getPromoEnd())) {
                        result.put("promoPrice", price.getPromoPrice());
                        result.put("isPromo", true);
                    } else {
                        result.put("retailPrice", price.getRetailPrice() != null ? price.getRetailPrice() : drug.getRetailPrice());
                        result.put("isPromo", false);
                    }
                } else {
                    result.put("promoPrice", price.getPromoPrice());
                    result.put("isPromo", false);
                }
            } else {
                result.put("retailPrice", drug.getRetailPrice());
                result.put("isPromo", false);
            }

            result.put("purchasePrice", price != null && price.getPurchasePrice() != null
                    ? price.getPurchasePrice() : drug.getPurchasePrice());
            result.put("memberPrice", price != null ? price.getMemberPrice() : null);

            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取当前价格失败：" + e.getMessage());
        }
    }

    private BigDecimal getOldPrice(Drug drug, String priceType) {
        DrugPrice price = priceMapper.getDrugPriceById(drug.getDrugId());
        if (price == null) {
            if ("purchase".equals(priceType)) {
                return drug.getPurchasePrice() != null ? drug.getPurchasePrice() : BigDecimal.ZERO;
            } else {
                return drug.getRetailPrice() != null ? drug.getRetailPrice() : BigDecimal.ZERO;
            }
        }
        if ("purchase".equals(priceType)) {
            return price.getPurchasePrice() != null ? price.getPurchasePrice()
                    : (drug.getPurchasePrice() != null ? drug.getPurchasePrice() : BigDecimal.ZERO);
        } else {
            return price.getRetailPrice() != null ? price.getRetailPrice()
                    : (drug.getRetailPrice() != null ? drug.getRetailPrice() : BigDecimal.ZERO);
        }
    }

    private void updatePrice(DrugPrice drugPrice, String priceType, BigDecimal newPrice) {
        if ("purchase".equals(priceType)) {
            drugPrice.setPurchasePrice(newPrice);
        } else if ("retail".equals(priceType)) {
            drugPrice.setRetailPrice(newPrice);
        } else if ("member".equals(priceType)) {
            drugPrice.setMemberPrice(newPrice);
        }
    }

    private void savePriceHistory(String drugId, String priceType, BigDecimal oldPrice,
                                   BigDecimal newPrice, String operator, String reason) {
        PriceHistory history = new PriceHistory();
        history.setHistoryId(UUIDUtil.getUUID());
        history.setDrugId(drugId);
        history.setPriceType(priceType);
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setChangeRate(oldPrice.compareTo(BigDecimal.ZERO) > 0
                ? newPrice.subtract(oldPrice).divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100))
                : BigDecimal.ZERO);
        history.setAdjustType("手动调整");
        history.setReason(reason);
        history.setOperator(operator);
        history.setCreateTime(new Date());

        priceMapper.savePriceHistory(history);
    }
}
