package com.example.drug.controller;

import com.example.drug.service.report.ReportService;
import com.example.drug.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    public Result getDailyReport(@RequestParam String startDate,
                                  @RequestParam String endDate,
                                  @RequestParam(required = false) String drugId) {
        return reportService.getDailyReport(startDate, endDate, drugId);
    }

    @GetMapping("/monthly")
    public Result getMonthlyReport(@RequestParam String startMonth,
                                    @RequestParam String endMonth,
                                    @RequestParam(required = false) String drugId) {
        return reportService.getMonthlyReport(startMonth, endMonth, drugId);
    }

    @GetMapping("/salesRanking")
    public Result getSalesRanking(@RequestParam String startDate,
                                   @RequestParam String endDate,
                                   @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return reportService.getSalesRanking(startDate, endDate, limit);
    }

    @GetMapping("/inventoryTurnover")
    public Result getInventoryTurnover(@RequestParam String startDate,
                                        @RequestParam String endDate,
                                        @RequestParam(required = false) String drugId) {
        return reportService.getInventoryTurnover(startDate, endDate, drugId);
    }

    @GetMapping("/summary")
    public Result getReportSummary(@RequestParam String startDate,
                                    @RequestParam String endDate) {
        return reportService.getReportSummary(startDate, endDate);
    }

    @GetMapping("/export/daily")
    public Result exportDailyReport(@RequestParam String startDate,
                                     @RequestParam String endDate,
                                     @RequestParam(required = false) String drugId) {
        return reportService.exportDailyReportExcel(startDate, endDate, drugId);
    }

    @GetMapping("/export/monthly")
    public Result exportMonthlyReport(@RequestParam String startMonth,
                                      @RequestParam String endMonth,
                                      @RequestParam(required = false) String drugId) {
        return reportService.exportMonthlyReportExcel(startMonth, endMonth, drugId);
    }

    @GetMapping("/export/salesRanking")
    public Result exportSalesRanking(@RequestParam String startDate,
                                      @RequestParam String endDate,
                                      @RequestParam(required = false, defaultValue = "100") Integer limit) {
        return reportService.exportSalesRankingExcel(startDate, endDate, limit);
    }

    @GetMapping("/export/inventoryTurnover")
    public Result exportInventoryTurnover(@RequestParam String startDate,
                                           @RequestParam String endDate,
                                           @RequestParam(required = false) String drugId) {
        return reportService.exportInventoryTurnoverExcel(startDate, endDate, drugId);
    }
}
