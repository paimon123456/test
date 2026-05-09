package com.example.drug.controller;

import com.example.drug.entity.SysPermission;
import com.example.drug.mapper.SysPermissionMapper;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @GetMapping("/list")
    public Result list() {
        return Result.success(sysPermissionMapper.selectList(null));
    }

    @PostMapping("/add")
    public Result add(@RequestBody SysPermission permission) {
        try {
            if (permission.getPermId() == null || permission.getPermId().isEmpty()) {
                permission.setPermId(UUID.randomUUID().toString().replace("-", ""));
            }
            sysPermissionMapper.insert(permission);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.fail("添加失败：" + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        try {
            sysPermissionMapper.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail("删除失败：" + e.getMessage());
        }
    }
}
