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
import com.elevate.fna.dto.PaymentReqDTO;
import com.elevate.fna.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
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
    
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<?>> getPaymentsByInvoice(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable Long invoiceId) {
        ApiResponse<?> response = paymentService.getPaymentsByInvoice(sessionToken, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<?>> getPaymentById(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.getPaymentById(sessionToken, paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPaymentsByTenant(@RequestHeader("X-Session-Token") String sessionToken) {
        ApiResponse<?> response = paymentService.getAllPaymentsByTenant(sessionToken);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/invoice/{invoiceId}/summary")
    public ResponseEntity<ApiResponse<?>> getPaymentSummary(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable Long invoiceId) {
        ApiResponse<?> response = paymentService.getPaymentSummary(sessionToken, invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<?>> updatePayment(@RequestHeader("X-Session-Token") String sessionToken, 
                                                       @PathVariable String paymentId, 
                                                       @RequestBody PaymentReqDTO paymentReqDTO) {
        ApiResponse<?> response = paymentService.updatePayment(sessionToken, paymentId, paymentReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<?>> deletePayment(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.deletePayment(sessionToken, paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}