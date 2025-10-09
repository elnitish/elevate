package com.elevate.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.dto.CustomerLedgerReqDTO;
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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<?>> listByCustomer(HttpServletRequest request, @PathVariable Long customerId) {
        ApiResponse<?> res = ledgerService.getEntriesByCustomer((String) request.getAttribute("tenantID"), customerId);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }
}


