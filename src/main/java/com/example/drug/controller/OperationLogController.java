package com.example.drug.controller;

import com.example.drug.entity.OperationLog;
import com.example.drug.service.OperationLogService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public Result list() {
        List<OperationLog> list = operationLogService.list();
        list.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return Result.success(list);
    }
}
