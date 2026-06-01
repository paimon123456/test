package com.example.drug.controller;

import com.example.drug.entity.SysPermission;
import com.example.drug.mapper.SysPermissionMapper;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    private String generateNextPermId() {
        List<SysPermission> list = sysPermissionMapper.selectList(null);
        int max = 0;
        for (SysPermission p : list) {
            String id = p.getPermId();
            if (id != null) {
                try {
                    int num = Integer.parseInt(id);
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        return String.valueOf(max + 1);
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(sysPermissionMapper.selectList(null));
    }

    @PostMapping("/add")
    public Result add(@RequestBody SysPermission permission) {
        try {
            if (permission.getPermId() == null || permission.getPermId().isEmpty()) {
                permission.setPermId(generateNextPermId());
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
