package com.elevate.fna.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.ExpenseSummaryDTO;
import com.elevate.fna.dto.MonthlyFinanceOverviewDTO;
import com.elevate.fna.dto.ProfitLossReportDTO;
import com.elevate.fna.service.FinanceReportService;

@RestController
@RequestMapping("/reports/finance")
public class FinanceReportController {

    @Autowired
    private FinanceReportService financeReportService;

    @GetMapping("/profit-loss/{yearMonth}")
    public ResponseEntity<ApiResponse<?>> getProfitLossReport(
            HttpServletRequest request,
            @PathVariable String yearMonth) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            ProfitLossReportDTO report = financeReportService.generateProfitLossReport(tenantId, yearMonth);
            ApiResponse<?> response = new ApiResponse<>(
                "Profit & Loss report generated successfully",
                HttpStatus.OK.value(),
                report
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<?> response = new ApiResponse<>(
                "Error generating report: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/monthly-overview/{yearMonth}")
    public ResponseEntity<ApiResponse<?>> getMonthlyOverview(
            HttpServletRequest request,
            @PathVariable String yearMonth) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            MonthlyFinanceOverviewDTO overview = financeReportService.generateMonthlyOverview(tenantId, yearMonth);
            ApiResponse<?> response = new ApiResponse<>(
                "Monthly finance overview generated successfully",
                HttpStatus.OK.value(),
                overview
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<?> response = new ApiResponse<>(
                "Error generating overview: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/expense-summary/{yearMonth}")
    public ResponseEntity<ApiResponse<?>> getExpenseSummary(
            HttpServletRequest request,
            @PathVariable String yearMonth) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            List<ExpenseSummaryDTO> summary = financeReportService.getExpenseSummary(tenantId, yearMonth);
            ApiResponse<?> response = new ApiResponse<>(
                "Expense summary generated successfully",
                HttpStatus.OK.value(),
                summary
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<?> response = new ApiResponse<>(
                "Error generating expense summary: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/year-to-date")
    public ResponseEntity<ApiResponse<?>> getYearToDateReport(
            HttpServletRequest request,
            @RequestParam int year) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            ProfitLossReportDTO report = financeReportService.generateYearToDateReport(tenantId, year);
            ApiResponse<?> response = new ApiResponse<>(
                "Year-to-date report generated successfully",
                HttpStatus.OK.value(),
                report
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<?> response = new ApiResponse<>(
                "Error generating year-to-date report: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
