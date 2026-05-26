package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.drug.entity.OperationLog;
import com.example.drug.mapper.OperationLogMapper;
import com.example.drug.service.OperationLogService;
import com.example.drug.util.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result queryLogs(Map<String, Object> params) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 操作人
        String username = (String) params.get("username");
        if (username != null && !username.isEmpty()) {
            wrapper.like(OperationLog::getUsername, username);
        }
        // 操作模块
        String module = (String) params.get("module");
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        // 操作类型
        String type = (String) params.get("type");
        if (type != null && !type.isEmpty()) {
            wrapper.eq(OperationLog::getType, type);
        }
        // 执行结果
        String result = (String) params.get("result");
        if (result != null && !result.isEmpty()) {
            wrapper.eq(OperationLog::getResult, result);
        }
        // 时间范围
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(OperationLog::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(OperationLog::getCreateTime, endDate + " 23:59:59");
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        
        // 使用 MyBatis-Plus 分页
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLog> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLog> resultPage = 
            operationLogMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", resultPage.getTotal());
        data.put("records", resultPage.getRecords());
        return Result.success(data);
    }

    @Override
    public void exportExcel(Map<String, Object> params, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
            
            String username = (String) params.get("username");
            if (username != null && !username.isEmpty()) {
                wrapper.like(OperationLog::getUsername, username);
            }
            String module = (String) params.get("module");
            if (module != null && !module.isEmpty()) {
                wrapper.eq(OperationLog::getModule, module);
            }
            String type = (String) params.get("type");
            if (type != null && !type.isEmpty()) {
                wrapper.eq(OperationLog::getType, type);
            }
            String result = (String) params.get("result");
            if (result != null && !result.isEmpty()) {
                wrapper.eq(OperationLog::getResult, result);
            }
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");
            if (startDate != null && !startDate.isEmpty()) {
                wrapper.ge(OperationLog::getCreateTime, startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                wrapper.le(OperationLog::getCreateTime, endDate + " 23:59:59");
            }
            
            wrapper.orderByDesc(OperationLog::getCreateTime);
            wrapper.last("LIMIT 10000");
            
            List<OperationLog> logs = operationLogMapper.selectList(wrapper);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("操作日志");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "操作人", "操作模块", "操作类型", "操作内容", "执行结果", "IP地址", "操作时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 数据行
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            
            int rowNum = 1;
            for (OperationLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(log.getUsername() != null ? log.getUsername() : "");
                row.createCell(2).setCellValue(log.getModule() != null ? log.getModule() : "");
                row.createCell(3).setCellValue(log.getType() != null ? log.getType() : "");
                row.createCell(4).setCellValue(log.getContent() != null ? log.getContent() : "");
                row.createCell(5).setCellValue(log.getResult() != null ? log.getResult() : "");
                row.createCell(6).setCellValue(log.getIp() != null ? log.getIp() : "");
                row.createCell(7).setCellValue(log.getCreateTime() != null ? sdf.format(log.getCreateTime()) : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int columnWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(columnWidth, 15000));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "操作日志_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            workbook.close();
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result cleanLogs(String startDate, String endDate, String operator) {
        try {
            LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
            if (startDate != null && !startDate.isEmpty()) {
                wrapper.ge(OperationLog::getCreateTime, startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                wrapper.le(OperationLog::getCreateTime, endDate + " 23:59:59");
            }
            int deleted = operationLogMapper.delete(wrapper);
            
            // 记录清理操作
            OperationLog log = new OperationLog();
            log.setLogId(com.example.drug.common.UUIDUtil.getUUID());
            log.setModule("系统日志");
            log.setType("删除");
            log.setUserId(operator);
            log.setUsername("系统");
            log.setContent("清理操作日志: 时间范围 " + startDate + " ~ " + endDate + "，删除 " + deleted + " 条记录");
            log.setResult("成功");
            log.setIp("127.0.0.1");
            log.setCreateTime(new Date());
            operationLogMapper.insert(log);
            
            return Result.success("清理完成，共删除 " + deleted + " 条日志");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("清理失败：" + e.getMessage());
        }
    }

    @Override
    public int autoCleanLogs(int retainMonths) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -retainMonths);
            String cutoffDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            
            LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(OperationLog::getCreateTime, cutoffDate + " 00:00:00");
            int deleted = operationLogMapper.delete(wrapper);
            
            if (deleted > 0) {
                OperationLog log = new OperationLog();
                log.setLogId(com.example.drug.common.UUIDUtil.getUUID());
                log.setModule("系统日志");
                log.setType("删除");
                log.setUserId("SYSTEM");
                log.setUsername("系统自动");
                log.setContent("自动清理 " + retainMonths + " 个月前的操作日志，删除 " + deleted + " 条记录");
                log.setResult("成功");
                log.setIp("127.0.0.1");
                log.setCreateTime(new Date());
                operationLogMapper.insert(log);
            }
            return deleted;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
