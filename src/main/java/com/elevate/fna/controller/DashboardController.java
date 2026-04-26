package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.fna.dto.AgingReportDTO;
import com.elevate.fna.dto.DashboardSummaryDTO;
import com.elevate.fna.dto.RevenueTrendDTO;
import com.elevate.fna.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Business dashboard and analytics")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Dashboard Summary", description = "Current month revenue, outstanding receivables, invoice counts, low stock, cash flow")
    public ResponseEntity<ApiResponse<?>> getSummary(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        DashboardSummaryDTO summary = dashboardService.getSummary(tenantId);
        return ResponseEntity.ok(new ApiResponse<>("Dashboard summary retrieved", 200, summary));
    }

    @GetMapping("/revenue-trend")
    @Operation(summary = "Revenue Trend", description = "Monthly revenue/expense/net income for the last N months")
    public ResponseEntity<ApiResponse<?>> getRevenueTrend(
            HttpServletRequest request,
            @RequestParam(defaultValue = "6") int months) {
        String tenantId = TenantContext.getTenantId(request);
        List<RevenueTrendDTO> trend = dashboardService.getRevenueTrend(tenantId, Math.min(months, 24));
        return ResponseEntity.ok(new ApiResponse<>("Revenue trend retrieved", 200, trend));
    }

    @GetMapping("/aging-report")
    @Operation(summary = "Aging Report", description = "Receivables aging buckets: current, 1-30, 31-60, 61-90, 90+ days with per-customer breakdown")
    public ResponseEntity<ApiResponse<?>> getAgingReport(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        AgingReportDTO report = dashboardService.getAgingReport(tenantId);
        return ResponseEntity.ok(new ApiResponse<>("Aging report retrieved", 200, report));
    }
}
