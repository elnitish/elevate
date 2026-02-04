package com.elevate.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.service.CustomerLedgerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/customerLedger")
public class CustomerLedgerController {

    private final CustomerLedgerService ledgerService;

    @Autowired
    public CustomerLedgerController(CustomerLedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/getEntriesByCustomerId/{customerId}") //working
    public ResponseEntity<ApiResponse<?>> getLedgerByCustomerId(HttpServletRequest request, @PathVariable Long customerId,
                                                                @RequestParam(required = false) Integer page,
                                                                @RequestParam(required = false) Integer size) {
        ApiResponse<?> res = ledgerService.getEntriesByCustomer((String) request.getAttribute("tenantID"), customerId, page, size);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @GetMapping("/getAllEntries") //working
    public ResponseEntity<ApiResponse<?>> getAllEntries(HttpServletRequest request,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer size) {
        ApiResponse<?> res = ledgerService.getAllEntries((String) request.getAttribute("tenantID"), page, size);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }
}


