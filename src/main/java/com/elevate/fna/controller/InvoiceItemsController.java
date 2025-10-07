package com.elevate.fna.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.fna.service.InvoiceItemsService;

@RestController
@RequestMapping("/invoice-items")
public class InvoiceItemsController {
    
    private final InvoiceItemsService invoiceItemsService;
    
    @Autowired
    public InvoiceItemsController(InvoiceItemsService invoiceItemsService) {
        this.invoiceItemsService = invoiceItemsService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createInvoiceItem(@RequestBody InvoiceItemReqDTO invoiceItemReqDTO) {
        ApiResponse<?> response = invoiceItemsService.createInvoiceItem(invoiceItemReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemsByInvoice(@PathVariable String tenantId, @PathVariable Long invoiceId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemsByInvoice(tenantId, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemsByProduct(@PathVariable String tenantId, @PathVariable String productId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemsByProduct(tenantId, productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/item/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemById(@PathVariable String tenantId, @PathVariable String invoiceItemId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemById(tenantId, invoiceItemId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> getAllInvoiceItemsByTenant(@PathVariable String tenantId) {
        ApiResponse<?> response = invoiceItemsService.getAllInvoiceItemsByTenant(tenantId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/tenant/{tenantId}/item/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> updateInvoiceItem(@PathVariable String tenantId, 
                                                          @PathVariable String invoiceItemId, 
                                                          @RequestBody InvoiceItemReqDTO invoiceItemReqDTO) {
        ApiResponse<?> response = invoiceItemsService.updateInvoiceItem(tenantId, invoiceItemId, invoiceItemReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/tenant/{tenantId}/item/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> deleteInvoiceItem(@PathVariable String tenantId, @PathVariable String invoiceItemId) {
        ApiResponse<?> response = invoiceItemsService.deleteInvoiceItem(tenantId, invoiceItemId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
