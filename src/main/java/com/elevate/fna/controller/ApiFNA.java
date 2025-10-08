package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.PaymentClassReqDTO;
import com.elevate.fna.entity.CustomerClass;
import com.elevate.fna.service.CustomerService;
import com.elevate.fna.service.InvoiceService;
import com.elevate.fna.dto.InvoiceReqDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class ApiFNA {

    private final InvoiceService invoiceService;
    private final CustomerService customerService;

    @Autowired
    public ApiFNA(InvoiceService invoiceService,CustomerService customerService) {
        this.invoiceService = invoiceService;
        this.customerService = customerService;
    }

    @PostMapping("/invoices")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceReqDTO invoiceReqDTO) {
        ApiResponse<?> response = invoiceService.createNewInvoice(invoiceReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/invoices")
    public ResponseEntity<?> getAllInvoices(@RequestHeader("X-Session-Token") String sessionToken, @RequestParam(required = false) String status) {
        if (status == null) {
            ApiResponse<?> response = invoiceService.returnAllInvoices(sessionToken);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        } else {
            ApiResponse<?> response = invoiceService.returnInvoicesWithStatus(sessionToken, status);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }
    }

    @PutMapping("/invoices/{id}/{status}")
    public ResponseEntity<?> updateInvoiceStatus(@RequestHeader("X-Session-Token") String sessionToken, @PathVariable Long id, @PathVariable String status) {
        ApiResponse<?> response = invoiceService.updateInvoiceStatus(sessionToken, id, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/invoices/{id}/status")
    public ResponseEntity<?> getInvoiceStatus(@PathVariable("id") long id) {
        ApiResponse<?> response = invoiceService.returnInvoiceStatus(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(@RequestBody PaymentClassReqDTO  paymentClassReqDTO) {
        ApiResponse<?> response = invoiceService.createNewPayment(paymentClassReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments() {
        ApiResponse<?> response = invoiceService.getAllPayments();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerClass customerClass) {
        ApiResponse<?> response = customerService.createNewCustomer(customerClass);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        ApiResponse<?> response = customerService.returnAllCustomers();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}