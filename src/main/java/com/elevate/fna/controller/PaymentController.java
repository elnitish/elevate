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
import com.elevate.fna.dto.PaymentReqDTO;
import com.elevate.fna.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPayment(@RequestBody PaymentReqDTO paymentReqDTO) {
        ApiResponse<?> response = paymentService.createPayment(paymentReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<?>> getPaymentsByInvoice(@PathVariable String tenantId, @PathVariable Long invoiceId) {
        ApiResponse<?> response = paymentService.getPaymentsByInvoice(tenantId, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/payment/{paymentId}")
    public ResponseEntity<ApiResponse<?>> getPaymentById(@PathVariable String tenantId, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.getPaymentById(tenantId, paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> getAllPaymentsByTenant(@PathVariable String tenantId) {
        ApiResponse<?> response = paymentService.getAllPaymentsByTenant(tenantId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/invoice/{invoiceId}/summary")
    public ResponseEntity<ApiResponse<?>> getPaymentSummary(@PathVariable String tenantId, @PathVariable Long invoiceId) {
        ApiResponse<?> response = paymentService.getPaymentSummary(tenantId, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/tenant/{tenantId}/payment/{paymentId}")
    public ResponseEntity<ApiResponse<?>> updatePayment(@PathVariable String tenantId, 
                                                       @PathVariable String paymentId, 
                                                       @RequestBody PaymentReqDTO paymentReqDTO) {
        ApiResponse<?> response = paymentService.updatePayment(tenantId, paymentId, paymentReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/tenant/{tenantId}/payment/{paymentId}")
    public ResponseEntity<ApiResponse<?>> deletePayment(@PathVariable String tenantId, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.deletePayment(tenantId, paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
