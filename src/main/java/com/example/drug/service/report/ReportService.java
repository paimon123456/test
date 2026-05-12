package com.example.drug.service.report;

import com.example.drug.entity.report.DailyReport;
import com.example.drug.entity.report.InventoryTurnover;
import com.example.drug.entity.report.SalesRanking;
import com.example.drug.util.Result;

import java.util.List;
import java.util.Map;

public interface ReportService {

    Result getDailyReport(String startDate, String endDate, String drugId);

    Result getMonthlyReport(String startMonth, String endMonth, String drugId);

    Result getSalesRanking(String startDate, String endDate, Integer limit);

    Result getInventoryTurnover(String startDate, String endDate, String drugId);

    Result getReportSummary(String startDate, String endDate);

    Result exportDailyReportExcel(String startDate, String endDate, String drugId);

    Result exportMonthlyReportExcel(String startMonth, String endMonth, String drugId);

    Result exportSalesRankingExcel(String startDate, String endDate, Integer limit);

    Result exportInventoryTurnoverExcel(String startDate, String endDate, String drugId);
}
