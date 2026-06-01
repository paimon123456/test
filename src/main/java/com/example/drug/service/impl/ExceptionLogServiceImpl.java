package com.example.drug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.drug.common.UUIDUtil;
import com.example.drug.entity.ExceptionLog;
import com.example.drug.entity.OperationLog;
import com.example.drug.mapper.ExceptionLogMapper;
import com.example.drug.mapper.OperationLogMapper;
import com.example.drug.service.ExceptionLogService;
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
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog> implements ExceptionLogService {

    @Autowired
    private ExceptionLogMapper exceptionLogMapper;
    
    @Autowired
    private OperationLogMapper operationLogMapper;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result queryLogs(Map<String, Object> params) {
        LambdaQueryWrapper<ExceptionLog> wrapper = new LambdaQueryWrapper<>();
        
        String username = (String) params.get("username");
        if (username != null && !username.isEmpty()) {
            wrapper.like(ExceptionLog::getUsername, username);
        }
        String exceptionType = (String) params.get("exceptionType");
        if (exceptionType != null && !exceptionType.isEmpty()) {
            wrapper.like(ExceptionLog::getExceptionType, exceptionType);
        }
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(ExceptionLog::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(ExceptionLog::getCreateTime, endDate + " 23:59:59");
        }
        
        wrapper.orderByDesc(ExceptionLog::getCreateTime);
        
        int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExceptionLog> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ExceptionLog> resultPage = 
            exceptionLogMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", resultPage.getTotal());
        data.put("records", resultPage.getRecords());
        return Result.success(data);
    }

    @Override
    public Result getDetail(String exceptionId) {
        ExceptionLog log = exceptionLogMapper.selectById(exceptionId);
        if (log == null) {
            return Result.fail("异常日志不存在");
        }
        return Result.success(log);
    }

    @Override
    public void exportExcel(Map<String, Object> params, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<ExceptionLog> wrapper = new LambdaQueryWrapper<>();
            
            String username = (String) params.get("username");
            if (username != null && !username.isEmpty()) {
                wrapper.like(ExceptionLog::getUsername, username);
            }
            String exceptionType = (String) params.get("exceptionType");
            if (exceptionType != null && !exceptionType.isEmpty()) {
                wrapper.like(ExceptionLog::getExceptionType, exceptionType);
            }
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");
            if (startDate != null && !startDate.isEmpty()) {
                wrapper.ge(ExceptionLog::getCreateTime, startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                wrapper.le(ExceptionLog::getCreateTime, endDate + " 23:59:59");
            }
            
            wrapper.orderByDesc(ExceptionLog::getCreateTime);
            wrapper.last("LIMIT 10000");
            
            List<ExceptionLog> logs = exceptionLogMapper.selectList(wrapper);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("异常日志");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "发生时间", "异常类型", "错误信息", "请求URL", "请求方式", "操作人", "IP地址"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ExceptionLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(log.getCreateTime() != null ? sdf.format(log.getCreateTime()) : "");
                row.createCell(2).setCellValue(log.getExceptionType() != null ? log.getExceptionType() : "");
                row.createCell(3).setCellValue(log.getErrorMessage() != null ? log.getErrorMessage() : "");
                row.createCell(4).setCellValue(log.getRequestUrl() != null ? log.getRequestUrl() : "");
                row.createCell(5).setCellValue(log.getRequestMethod() != null ? log.getRequestMethod() : "");
                row.createCell(6).setCellValue(log.getUsername() != null ? log.getUsername() : "");
                row.createCell(7).setCellValue(log.getIp() != null ? log.getIp() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int columnWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(columnWidth, 15000));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "异常日志_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
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
            LambdaQueryWrapper<ExceptionLog> wrapper = new LambdaQueryWrapper<>();
            if (startDate != null && !startDate.isEmpty()) {
                wrapper.ge(ExceptionLog::getCreateTime, startDate + " 00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                wrapper.le(ExceptionLog::getCreateTime, endDate + " 23:59:59");
            }
            int deleted = exceptionLogMapper.delete(wrapper);
            
            // 记录清理操作
            OperationLog log = new OperationLog();
            log.setLogId(UUIDUtil.getUUID());
            log.setModule("系统日志");
            log.setType("删除");
            log.setUserId(operator);
            log.setUsername("系统");
            log.setContent("清理异常日志: 时间范围 " + startDate + " ~ " + endDate + "，删除 " + deleted + " 条记录");
            log.setResult("成功");
            log.setIp("127.0.0.1");
            log.setCreateTime(new Date());
            operationLogMapper.insert(log);
            
            return Result.success("清理完成，共删除 " + deleted + " 条异常日志");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("清理失败：" + e.getMessage());
        }
    }
}
