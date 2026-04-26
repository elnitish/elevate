package com.elevate.insc.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.insc.dto.WarehouseReqDTO;
import com.elevate.insc.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouses")
@Tag(name = "Warehouse Management", description = "Manage warehouses and storage locations")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @Operation(summary = "Create Warehouse", description = "Create a new warehouse for the tenant")
    public ResponseEntity<ApiResponse<?>> createWarehouse(
            HttpServletRequest request,
            @Valid @RequestBody WarehouseReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseService.createWarehouse(tenantId, dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping
    @Operation(summary = "Get All Warehouses", description = "Retrieve all warehouses for the tenant")
    public ResponseEntity<ApiResponse<?>> getAllWarehouses(HttpServletRequest request) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseService.getAllWarehouses(tenantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{warehouseId}")
    @Operation(summary = "Get Warehouse by ID", description = "Retrieve a specific warehouse")
    public ResponseEntity<ApiResponse<?>> getWarehouseById(
            HttpServletRequest request,
            @PathVariable String warehouseId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseService.getWarehouseById(tenantId, warehouseId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/{warehouseId}")
    @Operation(summary = "Update Warehouse", description = "Update an existing warehouse")
    public ResponseEntity<ApiResponse<?>> updateWarehouse(
            HttpServletRequest request,
            @PathVariable String warehouseId,
            @Valid @RequestBody WarehouseReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseService.updateWarehouse(tenantId, warehouseId, dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/{warehouseId}/deactivate")
    @Operation(summary = "Deactivate Warehouse", description = "Deactivate a warehouse (cannot deactivate default)")
    public ResponseEntity<ApiResponse<?>> deactivateWarehouse(
            HttpServletRequest request,
            @PathVariable String warehouseId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseService.deactivateWarehouse(tenantId, warehouseId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
