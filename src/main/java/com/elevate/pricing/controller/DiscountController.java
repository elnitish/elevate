package com.elevate.pricing.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.pricing.dto.DiscountReqDTO;
import com.elevate.pricing.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
@Tag(name = "Discounts", description = "Manage discount rules and promotions")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    @Operation(summary = "Create Discount")
    public ResponseEntity<ApiResponse<?>> createDiscount(
            HttpServletRequest request,
            @Valid @RequestBody DiscountReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = discountService.createDiscount(tenantId, dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping
    @Operation(summary = "Get All Discounts")
    public ResponseEntity<ApiResponse<?>> getAllDiscounts(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = discountService.getAllDiscounts(tenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Discounts")
    public ResponseEntity<ApiResponse<?>> getActiveDiscounts(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = discountService.getActiveDiscounts(tenantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{discountId}/deactivate")
    @Operation(summary = "Deactivate Discount")
    public ResponseEntity<ApiResponse<?>> deactivateDiscount(
            HttpServletRequest request,
            @PathVariable String discountId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = discountService.deactivateDiscount(tenantId, discountId);
        return ResponseEntity.ok(response);
    }
}
