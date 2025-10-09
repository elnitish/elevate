package com.elevate.fna.controller;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/createPayment") //working
    public ResponseEntity<ApiResponse<?>> createPayment(HttpServletRequest request, @RequestBody PaymentReqDTO paymentReqDTO) {
        ApiResponse<?> response = paymentService.createPayment((String) request.getAttribute("tenantID"), paymentReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getPaymentByInvoice/{invoiceId}") //working
    public ResponseEntity<ApiResponse<?>> getPaymentsByInvoice(HttpServletRequest request, @PathVariable Long invoiceId) {
        ApiResponse<?> response = paymentService.getPaymentsByInvoice((String) request.getAttribute("tenantID"), invoiceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getPaymentById/{paymentId}") //working
    public ResponseEntity<ApiResponse<?>> getPaymentById(HttpServletRequest request, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.getPaymentById((String) request.getAttribute("tenantID"), paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getAllPayments") //working
    public ResponseEntity<ApiResponse<?>> getAllPayments(HttpServletRequest request) {
        ApiResponse<?> response = paymentService.returnAllPayments((String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @DeleteMapping("/deletePayment/{paymentId}") //working
    public ResponseEntity<ApiResponse<?>> deletePayment(HttpServletRequest request, @PathVariable String paymentId) {
        ApiResponse<?> response = paymentService.deletePayment((String) request.getAttribute("tenantID"), paymentId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}