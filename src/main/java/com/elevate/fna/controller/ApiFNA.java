package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.InvoiceService;
import com.elevate.fna.dto.InvoiceReqDTO;
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

    @PostMapping("/createInvoice")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceReqDTO invoiceReqDTO) {
        ApiResponse<?> response = invoiceService.createNewInvoice(invoiceReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/getAllInvoices")
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

    @PutMapping("/invoice/{id}/{status}")
    public ResponseEntity<?> updateInvoice(@PathVariable("id") long id, @PathVariable("status") String status) {
        ApiResponse<?> response = invoiceService.updateInvoiceStatus(id, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
