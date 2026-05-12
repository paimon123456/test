package com.example.drug.service.impl;

import com.example.drug.entity.report.DailyReport;
import com.example.drug.entity.report.InventoryTurnover;
import com.example.drug.entity.report.SalesRanking;
import com.example.drug.mapper.ReportMapper;
import com.example.drug.service.report.ReportService;
import com.example.drug.util.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public Result getDailyReport(String startDate, String endDate, String drugId) {
        try {
            List<DailyReport> reports = reportMapper.getDailyReport(startDate, endDate, drugId);
            Map<String, Object> summary = calculateSummary(reports);
            Map<String, Object> result = new HashMap<>();
            result.put("list", reports);
            result.put("summary", summary);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取日报失败：" + e.getMessage());
        }
    }

    @Override
    public Result getMonthlyReport(String startMonth, String endMonth, String drugId) {
        try {
            List<DailyReport> reports = reportMapper.getMonthlyReport(startMonth, endMonth, drugId);
            Map<String, Object> summary = calculateSummary(reports);
            Map<String, Object> result = new HashMap<>();
            result.put("list", reports);
            result.put("summary", summary);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取月报失败：" + e.getMessage());
        }
    }

    @Override
    public Result getSalesRanking(String startDate, String endDate, Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 20;
            }
            List<SalesRanking> rankings = reportMapper.getSalesRanking(startDate, endDate, limit);
            Integer totalAmount = reportMapper.getTotalSalesAmount(startDate, endDate);

            for (int i = 0; i < rankings.size(); i++) {
                rankings.get(i).setRank(i + 1);
                if (totalAmount != null && totalAmount > 0) {
                    BigDecimal proportion = rankings.get(i).getTotalSalesAmount()
                            .divide(new BigDecimal(totalAmount), 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    rankings.get(i).setProportion(proportion);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("list", rankings);
            result.put("totalAmount", totalAmount);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("获取销售排行失败：" + e.getMessage());
        }
    }

    @Override
    public Result getInventoryTurnover(String startDate, String endDate, String drugId) {
        try {
            List<InventoryTurnover> turnovers = reportMapper.getInventoryTurnover(startDate, endDate, drugId);
            return Result.success(turnovers);
        } catch (Exception e) {
            return Result.fail("获取库存周转率失败：" + e.getMessage());
        }
    }

    @Override
    public Result getReportSummary(String startDate, String endDate) {
        try {
            List<DailyReport> dailyReports = reportMapper.getDailyReport(startDate, endDate, null);
            Map<String, Object> summary = calculateSummary(dailyReports);

            List<SalesRanking> rankings = reportMapper.getSalesRanking(startDate, endDate, 10);
            summary.put("topSales", rankings);

            return Result.success(summary);
        } catch (Exception e) {
            return Result.fail("获取报表汇总失败：" + e.getMessage());
        }
    }

    private Map<String, Object> calculateSummary(List<DailyReport> reports) {
        Map<String, Object> summary = new HashMap<>();
        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalSale = BigDecimal.ZERO;
        int totalPurchaseNum = 0;
        int totalSaleNum = 0;
        int totalClosingStock = 0;

        for (DailyReport report : reports) {
            if (report.getPurchaseAmount() != null) {
                totalPurchase = totalPurchase.add(report.getPurchaseAmount());
            }
            if (report.getSaleAmount() != null) {
                totalSale = totalSale.add(report.getSaleAmount());
            }
            if (report.getPurchaseIn() != null) {
                totalPurchaseNum += report.getPurchaseIn();
            }
            if (report.getSaleOut() != null) {
                totalSaleNum += report.getSaleOut();
            }
            if (report.getClosingStock() != null) {
                totalClosingStock += report.getClosingStock();
            }
        }

        summary.put("totalPurchase", totalPurchase);
        summary.put("totalSale", totalSale);
        summary.put("totalProfit", totalSale.subtract(totalPurchase));
        summary.put("totalPurchaseNum", totalPurchaseNum);
        summary.put("totalSaleNum", totalSaleNum);
        summary.put("totalClosingStock", totalClosingStock);
        summary.put("reportCount", reports.size());

        return summary;
    }

    @Override
    public Result exportDailyReportExcel(String startDate, String endDate, String drugId) {
        try {
            List<DailyReport> reports = reportMapper.getDailyReport(startDate, endDate, drugId);
            String fileName = "进销存日报_" + startDate + "_" + endDate + ".xlsx";
            exportDailyReportToExcel(reports, fileName);
            return Result.success("导出成功，文件：" + fileName);
        } catch (Exception e) {
            return Result.fail("导出日报Excel失败：" + e.getMessage());
        }
    }

    @Override
    public Result exportMonthlyReportExcel(String startMonth, String endMonth, String drugId) {
        try {
            List<DailyReport> reports = reportMapper.getMonthlyReport(startMonth, endMonth, drugId);
            String fileName = "进销存月报_" + startMonth + "_" + endMonth + ".xlsx";
            exportDailyReportToExcel(reports, fileName);
            return Result.success("导出成功，文件：" + fileName);
        } catch (Exception e) {
            return Result.fail("导出月报Excel失败：" + e.getMessage());
        }
    }

    @Override
    public Result exportSalesRankingExcel(String startDate, String endDate, Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 100;
            }
            List<SalesRanking> rankings = reportMapper.getSalesRanking(startDate, endDate, limit);
            Integer totalAmount = reportMapper.getTotalSalesAmount(startDate, endDate);

            for (int i = 0; i < rankings.size(); i++) {
                rankings.get(i).setRank(i + 1);
                if (totalAmount != null && totalAmount > 0) {
                    BigDecimal proportion = rankings.get(i).getTotalSalesAmount()
                            .divide(new BigDecimal(totalAmount), 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    rankings.get(i).setProportion(proportion);
                }
            }

            String fileName = "销售排行_" + startDate + "_" + endDate + ".xlsx";
            exportSalesRankingToExcel(rankings, fileName);
            return Result.success("导出成功，文件：" + fileName);
        } catch (Exception e) {
            return Result.fail("导出销售排行Excel失败：" + e.getMessage());
        }
    }

    @Override
    public Result exportInventoryTurnoverExcel(String startDate, String endDate, String drugId) {
        try {
            List<InventoryTurnover> turnovers = reportMapper.getInventoryTurnover(startDate, endDate, drugId);
            String fileName = "库存周转率_" + startDate + "_" + endDate + ".xlsx";
            exportInventoryTurnoverToExcel(turnovers, fileName);
            return Result.success("导出成功，文件：" + fileName);
        } catch (Exception e) {
            return Result.fail("导出库存周转率Excel失败：" + e.getMessage());
        }
    }

    private void exportDailyReportToExcel(List<DailyReport> reports, String fileName) throws Exception {
        HttpServletResponse response = getHttpServletResponse();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("进销存报表");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"药品名称", "规格", "单位", "期初库存", "采购入库", "销售出库", "退货入库", "报废出库", "期末库存", "采购金额", "销售金额", "利润"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        int rowNum = 1;
        for (DailyReport report : reports) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, report.getDrugName(), dataStyle);
            createCell(row, 1, report.getSpecification(), dataStyle);
            createCell(row, 2, report.getUnit(), dataStyle);
            createCell(row, 3, report.getOpeningStock(), dataStyle);
            createCell(row, 4, report.getPurchaseIn(), dataStyle);
            createCell(row, 5, report.getSaleOut(), dataStyle);
            createCell(row, 6, report.getReturnIn(), dataStyle);
            createCell(row, 7, report.getReturnOut(), dataStyle);
            createCell(row, 8, report.getClosingStock(), dataStyle);
            createMoneyCell(row, 9, report.getPurchaseAmount(), moneyStyle);
            createMoneyCell(row, 10, report.getSaleAmount(), moneyStyle);
            createMoneyCell(row, 11, report.getProfit(), moneyStyle);
        }

        Row sumRow = sheet.createRow(rowNum);
        CellStyle sumStyle = createHeaderStyle(workbook);

        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalSale = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (DailyReport report : reports) {
            if (report.getPurchaseAmount() != null) totalPurchase = totalPurchase.add(report.getPurchaseAmount());
            if (report.getSaleAmount() != null) totalSale = totalSale.add(report.getSaleAmount());
        }
        totalProfit = totalSale.subtract(totalPurchase);

        createCell(sumRow, 0, "合计", sumStyle);
        for (int i = 1; i < 9; i++) {
            createCell(sumRow, i, "", sumStyle);
        }
        createMoneyCell(sumRow, 9, totalPurchase, moneyStyle);
        createMoneyCell(sumRow, 10, totalSale, moneyStyle);
        createMoneyCell(sumRow, 11, totalProfit, moneyStyle);

        writeResponse(workbook, fileName);
    }

    private void exportSalesRankingToExcel(List<SalesRanking> rankings, String fileName) throws Exception {
        HttpServletResponse response = getHttpServletResponse();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("销售排行");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"排名", "药品名称", "规格", "单位", "销售数量", "销售金额", "占比(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        int rowNum = 1;
        for (SalesRanking ranking : rankings) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, ranking.getRank(), dataStyle);
            createCell(row, 1, ranking.getDrugName(), dataStyle);
            createCell(row, 2, ranking.getSpecification(), dataStyle);
            createCell(row, 3, ranking.getUnit(), dataStyle);
            createCell(row, 4, ranking.getTotalSalesNum(), dataStyle);
            createMoneyCell(row, 5, ranking.getTotalSalesAmount(), moneyStyle);
            createCell(row, 6, ranking.getProportion() != null ? ranking.getProportion().toString() + "%" : "0%", dataStyle);
        }

        writeResponse(workbook, fileName);
    }

    private void exportInventoryTurnoverToExcel(List<InventoryTurnover> turnovers, String fileName) throws Exception {
        HttpServletResponse response = getHttpServletResponse();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("库存周转率");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"药品名称", "规格", "平均库存", "总销售量", "总采购量", "周转率(%)", "周转天数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        int rowNum = 1;
        for (InventoryTurnover turnover : turnovers) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, turnover.getDrugName(), dataStyle);
            createCell(row, 1, turnover.getSpecification(), dataStyle);
            createCell(row, 2, turnover.getAverageStock(), dataStyle);
            createCell(row, 3, turnover.getTotalSales(), dataStyle);
            createCell(row, 4, turnover.getTotalPurchase(), dataStyle);
            createCell(row, 5, turnover.getTurnoverRate() != null ? turnover.getTurnoverRate().toString() + "%" : "0%", dataStyle);
            createCell(row, 6, turnover.getTurnoverDays(), dataStyle);
        }

        writeResponse(workbook, fileName);
    }

    private jakarta.servlet.http.HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("¥#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    private void createMoneyCell(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0.0);
        }
        cell.setCellStyle(style);
    }

    private void writeResponse(Workbook workbook, String fileName) throws Exception {
        HttpServletResponse response = getHttpServletResponse();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }
}
