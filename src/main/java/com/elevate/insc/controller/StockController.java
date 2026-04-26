package com.elevate.insc.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.config.TenantContext;
import com.elevate.insc.dto.*;
import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.service.StockLevelService;
import com.elevate.insc.service.StockMovementService;
import com.elevate.insc.service.WarehouseTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/stock")
@Tag(name = "Stock Management", description = "Stock levels, movements, and low stock alerts")
public class StockController {

    private final StockLevelService stockLevelService;
    private final StockMovementService stockMovementService;
    private final WarehouseTransferService warehouseTransferService;

    @Autowired
    public StockController(StockLevelService stockLevelService,
            StockMovementService stockMovementService,
            WarehouseTransferService warehouseTransferService) {
        this.stockLevelService = stockLevelService;
        this.stockMovementService = stockMovementService;
        this.warehouseTransferService = warehouseTransferService;
    }

    // ==================== STOCK LEVELS ====================

    @GetMapping("/levels")
    @Operation(summary = "Get All Stock Levels", description = "Retrieve current stock levels for all products in the tenant, optionally filtered by warehouse")
    public ResponseEntity<ApiResponse<?>> getAllStockLevels(
            HttpServletRequest request,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        Page<StockLevelClass> stockLevels;
        if (warehouseId != null && !warehouseId.isBlank()) {
            stockLevels = stockLevelService.getStockLevelsByWarehouse(tenantId, warehouseId, pageable);
        } else {
            stockLevels = stockLevelService.getStockLevelsByTenant(tenantId, pageable);
        }

        Page<StockLevelResDTO> response = stockLevels.map(this::toStockLevelDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock levels retrieved successfully", 200, response));
    }

    @GetMapping("/levels/product/{productId}")
    @Operation(summary = "Get Stock Level by Product", description = "Retrieve current stock level for a specific product")
    public ResponseEntity<ApiResponse<?>> getStockLevelByProduct(
            @Parameter(description = "Product ID") @PathVariable String productId,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        StockLevelClass stockLevel = stockLevelService.getStockLevel(tenantId, productId)
                .orElse(null);

        if (stockLevel == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("Stock level not found for product", 404, null));
        }

        StockLevelResDTO response = toStockLevelDTO(stockLevel);
        return ResponseEntity.ok(new ApiResponse<>("Stock level retrieved successfully", 200, response));
    }

    @PutMapping("/levels/adjust")
    @Operation(summary = "Adjust Stock Level", description = "Manually adjust stock level for a product")
    public ResponseEntity<ApiResponse<?>> adjustStockLevel(
            @Valid @RequestBody StockAdjustmentReqDTO adjustmentReq,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        stockLevelService.updateStockLevel(tenantId, adjustmentReq.getProductId(), adjustmentReq.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>("Stock level adjusted successfully", 200, null));
    }

    // ==================== LOW STOCK ALERTS ====================

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Get Low Stock Alerts", description = "Retrieve products with stock levels below their configured reorder point, or below a custom threshold")
    public ResponseEntity<ApiResponse<?>> getLowStockAlerts(
            @Parameter(description = "Custom threshold (if omitted, uses per-product reorder_point)") @RequestParam(required = false) Integer threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        Page<StockLevelClass> lowStockProducts;
        if (threshold != null) {
            lowStockProducts = stockLevelService.getLowStockProducts(tenantId, threshold, pageable);
        } else {
            lowStockProducts = stockLevelService.getLowStockByReorderPoint(tenantId, pageable);
        }

        Page<LowStockAlertDTO> alerts = lowStockProducts.map(sl -> {
            String productName = sl.getProduct() != null ? sl.getProduct().getName() : "Unknown Product";
            int effectiveThreshold = threshold != null ? threshold : sl.getReorderPoint();
            String alertLevel;
            if (sl.getQuantity() == 0) {
                alertLevel = "OUT_OF_STOCK";
            } else if (sl.getQuantity() < effectiveThreshold / 2) {
                alertLevel = "CRITICAL";
            } else {
                alertLevel = "LOW";
            }
            return new LowStockAlertDTO(
                    sl.getProductId(),
                    productName,
                    sl.getQuantity(),
                    effectiveThreshold,
                    effectiveThreshold - sl.getQuantity(),
                    alertLevel);
        });

        return ResponseEntity.ok(new ApiResponse<>("Low stock alerts retrieved successfully", 200, alerts));
    }

    // ==================== STOCK MOVEMENTS ====================

    @GetMapping("/movements")
    @Operation(summary = "Get All Stock Movements", description = "Retrieve all stock movements (IN/OUT) for the tenant")
    public ResponseEntity<ApiResponse<?>> getAllStockMovements(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByTenantPaged(tenantId, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    @GetMapping("/movements/product/{productId}")
    @Operation(summary = "Get Stock Movements by Product", description = "Retrieve all stock movements for a specific product")
    public ResponseEntity<ApiResponse<?>> getStockMovementsByProduct(
            @Parameter(description = "Product ID") @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByProductPaged(tenantId, productId, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    @GetMapping("/movements/type/{type}")
    @Operation(summary = "Get Stock Movements by Type", description = "Retrieve stock movements filtered by type (IN or OUT)")
    public ResponseEntity<ApiResponse<?>> getStockMovementsByType(
            @Parameter(description = "Movement type (IN or OUT)") @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        StockMovementClass.Type movementType = StockMovementClass.Type.valueOf(type.toUpperCase());
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByTypePaged(tenantId, movementType, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    @GetMapping("/movements/date-range")
    @Operation(summary = "Get Stock Movements by Date Range", description = "Retrieve stock movements within a specific date range")
    public ResponseEntity<ApiResponse<?>> getStockMovementsByDateRange(
            @Parameter(description = "Start date (ISO format)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByDateRangePaged(tenantId, startDate, endDate, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    @GetMapping("/movements/purchase-order/{purchaseOrderId}")
    @Operation(summary = "Get Stock Movements by Purchase Order", description = "Retrieve all stock movements related to a specific purchase order")
    public ResponseEntity<ApiResponse<?>> getStockMovementsByPurchaseOrder(
            @Parameter(description = "Purchase Order ID") @PathVariable String purchaseOrderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByPurchaseOrderPaged(tenantId, purchaseOrderId, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    @GetMapping("/movements/invoice/{invoiceId}")
    @Operation(summary = "Get Stock Movements by Invoice", description = "Retrieve all stock movements related to a specific invoice")
    public ResponseEntity<ApiResponse<?>> getStockMovementsByInvoice(
            @Parameter(description = "Invoice ID") @PathVariable String invoiceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<StockMovementClass> movements = stockMovementService.getStockMovementsByInvoicePaged(tenantId, invoiceId, pageable);

        Page<StockMovementResDTO> response = movements.map(this::toMovementDTO);
        return ResponseEntity.ok(new ApiResponse<>("Stock movements retrieved successfully", 200, response));
    }

    // ==================== WAREHOUSE TRANSFERS ====================

    @PostMapping("/transfers")
    @Operation(summary = "Create Warehouse Transfer", description = "Initiate a stock transfer between warehouses")
    public ResponseEntity<ApiResponse<?>> createTransfer(
            HttpServletRequest request,
            @Valid @RequestBody WarehouseTransferReqDTO dto) {
        String tenantId = TenantContext.getTenantId(request);
        String initiatedBy = (String) request.getAttribute("sessionKey");
        ApiResponse<?> response = warehouseTransferService.createTransfer(tenantId, initiatedBy, dto);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/transfers/{transferId}/complete")
    @Operation(summary = "Complete Warehouse Transfer", description = "Complete a pending transfer — moves stock between warehouses")
    public ResponseEntity<ApiResponse<?>> completeTransfer(
            HttpServletRequest request,
            @PathVariable String transferId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseTransferService.completeTransfer(tenantId, transferId);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/transfers/{transferId}/cancel")
    @Operation(summary = "Cancel Warehouse Transfer", description = "Cancel a pending or in-transit transfer")
    public ResponseEntity<ApiResponse<?>> cancelTransfer(
            HttpServletRequest request,
            @PathVariable String transferId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseTransferService.cancelTransfer(tenantId, transferId);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/transfers")
    @Operation(summary = "Get Warehouse Transfers", description = "List all transfers, optionally filtered by status")
    public ResponseEntity<ApiResponse<?>> getTransfers(
            HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String tenantId = TenantContext.getTenantId(request);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        ApiResponse<?> response = warehouseTransferService.getTransfers(tenantId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transfers/{transferId}")
    @Operation(summary = "Get Transfer by ID")
    public ResponseEntity<ApiResponse<?>> getTransferById(
            HttpServletRequest request,
            @PathVariable String transferId) {
        String tenantId = TenantContext.getTenantId(request);
        ApiResponse<?> response = warehouseTransferService.getTransferById(tenantId, transferId);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatusCode.valueOf(response.getCode()));
    }

    // ==================== HELPERS ====================

    private StockLevelResDTO toStockLevelDTO(StockLevelClass sl) {
        return new StockLevelResDTO(
                sl.getId(),
                sl.getProductId(),
                sl.getProduct() != null ? sl.getProduct().getName() : "Unknown Product",
                sl.getWarehouseId(),
                sl.getWarehouse() != null ? sl.getWarehouse().getName() : null,
                sl.getQuantity(),
                sl.getReorderPoint(),
                sl.getReorderQuantity(),
                sl.getUpdatedAt(),
                sl.getQuantity() <= sl.getReorderPoint(),
                sl.getReorderPoint());
    }

    private StockMovementResDTO toMovementDTO(StockMovementClass movement) {
        String productName = movement.getProduct() != null ? movement.getProduct().getName() : "Unknown Product";
        return new StockMovementResDTO(
                movement.getId(),
                movement.getProductId(),
                productName,
                movement.getPurchaseOrderId(),
                movement.getInvoiceId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getDate(),
                movement.getReference());
    }
}
