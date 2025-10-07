package com.elevate.insc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.PurchaseOrderReqDTO;
import com.elevate.insc.service.PurchaseOrderService;

@RestController
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {
    
    private final PurchaseOrderService purchaseOrderService;
    
    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPurchaseOrder(@RequestBody PurchaseOrderReqDTO purchaseOrderReqDTO) {
        ApiResponse<?> response = purchaseOrderService.createPurchaseOrder(purchaseOrderReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> getPurchaseOrdersByTenant(@PathVariable String tenantId) {
        ApiResponse<?> response = purchaseOrderService.getPurchaseOrdersByTenant(tenantId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/order/{purchaseOrderId}")
    public ResponseEntity<ApiResponse<?>> getPurchaseOrderById(@PathVariable String tenantId, @PathVariable String purchaseOrderId) {
        ApiResponse<?> response = purchaseOrderService.getPurchaseOrderById(tenantId, purchaseOrderId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/tenant/{tenantId}/order/{purchaseOrderId}/status")
    public ResponseEntity<ApiResponse<?>> updatePurchaseOrderStatus(@PathVariable String tenantId, 
                                                                   @PathVariable String purchaseOrderId, 
                                                                   @RequestParam String status) {
        ApiResponse<?> response = purchaseOrderService.updatePurchaseOrderStatus(tenantId, purchaseOrderId, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<?>> getPurchaseOrdersBySupplier(@PathVariable String tenantId, @PathVariable String supplierId) {
        ApiResponse<?> response = purchaseOrderService.getPurchaseOrdersBySupplier(tenantId, supplierId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/status/{status}")
    public ResponseEntity<ApiResponse<?>> getPurchaseOrdersByStatus(@PathVariable String tenantId, @PathVariable String status) {
        ApiResponse<?> response = purchaseOrderService.getPurchaseOrdersByStatus(tenantId, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
