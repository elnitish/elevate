package com.elevate.pricing.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.pricing.dto.PriceListItemReqDTO;
import com.elevate.pricing.dto.PriceListReqDTO;
import com.elevate.pricing.service.PriceListService;
import com.elevate.pricing.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/price-lists")
@Tag(name = "Pricing", description = "Price lists, tiered pricing, and price resolution")
public class PriceListController {

    private final PriceListService priceListService;
    private final PricingService pricingService;

    @Autowired
    public PriceListController(PriceListService priceListService, PricingService pricingService) {
        this.priceListService = priceListService;
        this.pricingService = pricingService;
    }

    @PostMapping
    @Operation(summary = "Create Price List")
    public ResponseEntity<ApiResponse<?>> createPriceList(
            HttpServletRequest request,
            @Valid @RequestBody PriceListReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.createPriceList(tenantId, dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping
    @Operation(summary = "Get All Price Lists")
    public ResponseEntity<ApiResponse<?>> getAllPriceLists(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.getAllPriceLists(tenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{priceListId}")
    @Operation(summary = "Get Price List by ID")
    public ResponseEntity<ApiResponse<?>> getPriceListById(
            HttpServletRequest request,
            @PathVariable String priceListId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.getPriceListById(tenantId, priceListId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/{priceListId}/items")
    @Operation(summary = "Add Item to Price List", description = "Add a product with price and quantity tier to a price list")
    public ResponseEntity<ApiResponse<?>> addItem(
            HttpServletRequest request,
            @PathVariable String priceListId,
            @Valid @RequestBody PriceListItemReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.addItem(tenantId, priceListId, dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/{priceListId}/items")
    @Operation(summary = "Get Price List Items")
    public ResponseEntity<ApiResponse<?>> getItems(
            HttpServletRequest request,
            @PathVariable String priceListId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.getItems(tenantId, priceListId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @DeleteMapping("/{priceListId}/items/{itemId}")
    @Operation(summary = "Delete Price List Item")
    public ResponseEntity<ApiResponse<?>> deleteItem(
            HttpServletRequest request,
            @PathVariable String priceListId,
            @PathVariable String itemId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.deleteItem(tenantId, priceListId, itemId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{priceListId}/deactivate")
    @Operation(summary = "Deactivate Price List")
    public ResponseEntity<ApiResponse<?>> deactivate(
            HttpServletRequest request,
            @PathVariable String priceListId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = priceListService.deactivatePriceList(tenantId, priceListId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resolve")
    @Operation(summary = "Resolve Price", description = "Get the effective price for a product given a customer and quantity")
    public ResponseEntity<ApiResponse<?>> resolvePrice(
            HttpServletRequest request,
            @RequestParam String productId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        String tenantId = TenantContext.getTenantId(request);
        var resolved = pricingService.resolvePrice(tenantId, productId, customerId, quantity);
        return ResponseEntity.ok(new ApiResponse<>("Price resolved successfully", 200, resolved));
    }
}
