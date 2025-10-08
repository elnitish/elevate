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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.fna.service.InvoiceItemsService;

@RestController
@RequestMapping("/api/invoice-items")
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
    
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemsByInvoice(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable Long invoiceId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemsByInvoice(sessionToken, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemsByProduct(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable String productId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemsByProduct(sessionToken, productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> getInvoiceItemById(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable String invoiceItemId) {
        ApiResponse<?> response = invoiceItemsService.getInvoiceItemById(sessionToken, invoiceItemId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllInvoiceItemsByTenant(@RequestHeader("X-Session-Token") String sessionToken) {
        ApiResponse<?> response = invoiceItemsService.getAllInvoiceItemsByTenant(sessionToken);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> updateInvoiceItem(@RequestHeader("X-Session-Token") String sessionToken, 
                                                          @PathVariable String invoiceItemId, 
                                                          @RequestBody InvoiceItemReqDTO invoiceItemReqDTO) {
        ApiResponse<?> response = invoiceItemsService.updateInvoiceItem(sessionToken, invoiceItemId, invoiceItemReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/{invoiceItemId}")
    public ResponseEntity<ApiResponse<?>> deleteInvoiceItem(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable String invoiceItemId) {
        ApiResponse<?> response = invoiceItemsService.deleteInvoiceItem(sessionToken, invoiceItemId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}