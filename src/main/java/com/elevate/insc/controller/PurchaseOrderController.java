package com.elevate.insc.controller;

import com.elevate.insc.dto.SupplierClassReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.PurchaseOrderReqDTO;
import com.elevate.insc.service.PurchaseOrderService;

@RestController
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {
    
    private final PurchaseOrderService purchaseOrderService;
    
    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }
    
    @PostMapping("/createNewPurchaseOrder")//working
    public ResponseEntity<ApiResponse<?>> createPurchaseOrder(HttpServletRequest request, @RequestBody PurchaseOrderReqDTO purchaseOrderReqDTO) {
        ApiResponse<?> response = purchaseOrderService.createNewPurchaseOrder((String) request.getAttribute("tenantID"),purchaseOrderReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getAllPurchaseOrder")//working
    public ResponseEntity<ApiResponse<?>> getAllPurchaseOrders(HttpServletRequest request) {
        ApiResponse<?> response = purchaseOrderService.returnAllPurchaseOrders((String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getPurchaseOrderById/{purchaseOrderId}")//working
    public ResponseEntity<ApiResponse<?>> getPurchaseOrderById(HttpServletRequest request, @PathVariable String purchaseOrderId) {
        ApiResponse<?> response = purchaseOrderService.returnPurchaseOrderById((String) request.getAttribute("tenantID"), purchaseOrderId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/updatePurchaseOrderStatus/{purchaseOrderId}/{status}")//working
    public ResponseEntity<ApiResponse<?>> updatePurchaseOrderStatus(HttpServletRequest request,
                                                                   @PathVariable String purchaseOrderId, 
                                                                   @PathVariable String status) {
        ApiResponse<?> response = purchaseOrderService.updatePurchaseOrderStatus((String)request.getAttribute("tenantID"), purchaseOrderId, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/createNewSupplier")//working
    public ResponseEntity<ApiResponse<?>> createNewSupplier(HttpServletRequest request, @RequestBody SupplierClassReqDTO supplierClassReqDTO) {
        ApiResponse<?> response = purchaseOrderService.createNewSupplier((String) request.getAttribute("tenantID"),supplierClassReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/getPurchaseOrderBySupplier/{supplierId}")//working
    public ResponseEntity<ApiResponse<?>> getPurchaseOrdersBySupplier(HttpServletRequest request, @PathVariable String supplierId) {
        ApiResponse<?> response = purchaseOrderService.returnPurchaseOrdersBySupplier((String) request.getAttribute("tenantID"), supplierId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getPurchaseOrderByStatus/{status}")//working
    public ResponseEntity<ApiResponse<?>> getPurchaseOrdersByStatus(HttpServletRequest request, @PathVariable String status) {
        ApiResponse<?> response = purchaseOrderService.returnPurchaseOrdersByStatus((String) request.getAttribute("tenantID"), status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}