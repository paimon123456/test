package com.example.drug.controller;

import com.example.drug.entity.Drug;
import com.example.drug.service.DrugService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drug")
public class DrugController {

    @Autowired
    private DrugService drugService;

    @PostMapping("/add")
    public Result add(@RequestBody Drug drug) {
        try {
            System.out.println("接收到的药品数据：" + drug);
            // 设置药品ID
            if (drug.getDrugId() == null || drug.getDrugId().isEmpty()) {
                drug.setDrugId(UUID.randomUUID().toString().replace("-", ""));
            }
            boolean success = drugService.save(drug);
            System.out.println("保存结果：" + success);
            return Result.success(success);
        } catch (Exception e) {
            System.err.println("添加药品异常：" + e.getMessage());
            e.printStackTrace();
            return Result.fail("添加失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(drugService.list());
    }

    @PostMapping("/update")
    public Result update(@RequestBody Drug drug) {
        try {
            System.out.println("接收到的修改数据：" + drug);
            boolean success = drugService.updateById(drug);
            System.out.println("修改结果：" + success);
            return Result.success(success);
        } catch (Exception e) {
            System.err.println("修改药品异常：" + e.getMessage());
            e.printStackTrace();
            return Result.fail("修改失败：" + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        boolean success = drugService.removeById(id);
        if (success) {
            drugService.resetIdSequence();
        }
        return Result.success(success);
    }

    @GetMapping("/status/{id}/{status}")
    public Result status(@PathVariable String id, @PathVariable Integer status) {
        Drug d = new Drug();
        d.setDrugId(id);
        d.setStatus(status);
        drugService.updateById(d);
        return Result.success("修改成功");
    }

    @GetMapping("/expiring")
    public Result expiring(@RequestParam(defaultValue = "90") Integer days) {
        return Result.success(drugService.getExpiringDrugs(days));
    }
}
