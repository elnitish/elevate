package com.elevate.insc.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.insc.dto.InventoryValuationDTO;
import com.elevate.insc.service.InventoryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports/inventory")
@Tag(name = "Inventory Reports", description = "Inventory valuation and analytics")
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;

    @Autowired
    public InventoryReportController(InventoryReportService inventoryReportService) {
        this.inventoryReportService = inventoryReportService;
    }

    @GetMapping("/valuation")
    @Operation(summary = "Inventory Valuation", description = "Total cost value, retail value, and potential profit of current inventory")
    public ResponseEntity<ApiResponse<?>> getValuation(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        InventoryValuationDTO valuation = inventoryReportService.getInventoryValuation(tenantId);
        return ResponseEntity.ok(new ApiResponse<>("Inventory valuation retrieved", 200, valuation));
    }
}
