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
public class ApiFNA {

    private final InvoiceService invoiceService;
    private final CustomerService customerService;

    @Autowired
    public ApiFNA(InvoiceService invoiceService,CustomerService customerService) {
        this.invoiceService = invoiceService;
        this.customerService = customerService;
    }

    @PostMapping("/fna/createInvoice")
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceReqDTO invoiceReqDTO) {
        ApiResponse<?> response = invoiceService.createNewInvoice(invoiceReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/fna/tenant/{tenantId}/invoices")
    public ResponseEntity<?> getAllInvoices(@PathVariable String tenantId, @RequestParam(required = false) String status) {
        if (status == null) {
            ApiResponse<?> response = invoiceService.returnAllInvoices(tenantId);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        } else {
            ApiResponse<?> response = invoiceService.returnInvoicesWithStatus(tenantId, status);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }
    }

    @PutMapping("/fna/tenant/{tenantId}/invoice/{id}/{status}")
    public ResponseEntity<?> updateInvoiceStatus(@PathVariable String tenantId, @PathVariable Long id, @PathVariable String status) {
        ApiResponse<?> response = invoiceService.updateInvoiceStatus(tenantId, id, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/fna/getInvoiceStatus/{id}")
    public ResponseEntity<?> getInvoiceStatus(@PathVariable("id") long id) {
        ApiResponse<?> response = invoiceService.returnInvoiceStatus(id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/fna/createPayment/")
    public ResponseEntity<?> createPayment(@RequestBody PaymentClassReqDTO  paymentClassReqDTO) {
        ApiResponse<?> response = invoiceService.createNewPayment(paymentClassReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/fna/getAllPayments")
    public ResponseEntity<?> getAllPayments() {
        ApiResponse<?> response = invoiceService.getAllPayments();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/fna/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerClass customerClass) {
        ApiResponse<?> response = customerService.createNewCustomer(customerClass);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/fna/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        ApiResponse<?> response = customerService.returnAllCustomers();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
