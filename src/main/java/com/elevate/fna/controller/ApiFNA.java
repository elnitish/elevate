package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.PaymentClassReqDTO;
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

    @Autowired
    public ApiFNA(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/fna/createInvoice")
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceReqDTO invoiceReqDTO) {
        ApiResponse<?> response = invoiceService.createNewInvoice(invoiceReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/fna/getAllInvoices")
    public ResponseEntity<?> getAllInvoices(@RequestParam(required = false) String status) {
        System.out.println(status);
        if (status==null) {
            ApiResponse<?> response = invoiceService.returnAllInvoices();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }
        else {
            ApiResponse<?> response = invoiceService.returnInvoicesWithStatus(status);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }
    }

    @PutMapping("/fna/invoice/{id}/{status}")
    public ResponseEntity<?> updateInvoice(@PathVariable("id") long id, @PathVariable("status") String status) {
        ApiResponse<?> response = invoiceService.updateInvoiceStatus(id, status);
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

    @GetMapping("/fna/getAllPayments/")
    public ResponseEntity<?> getAllPayments() {
        ApiResponse<?> response = invoiceService.getAllPayments();
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}
