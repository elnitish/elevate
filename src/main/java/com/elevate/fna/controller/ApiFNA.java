package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.InvoiceService;
import com.elevate.fna.dto.InvoiceReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/getAllInvoices")
    public ResponseEntity<?> getAllInvoices() {
        ApiResponse<?> response = invoiceService.returnAllInvoices();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getCode()));
    }
}
