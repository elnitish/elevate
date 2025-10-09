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
import com.elevate.crm.dto.CustomerBalanceUpdateReqDTO;
import com.elevate.crm.service.CustomerBalanceService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/customerBalance")
public class CustomerBalanceController {

    private final CustomerBalanceService customerBalanceService;

    @Autowired
    public CustomerBalanceController(CustomerBalanceService customerBalanceService) {
        this.customerBalanceService = customerBalanceService;
    }

    @GetMapping("/getAllBalances")
    public ResponseEntity<ApiResponse<?>> getAllBalances(HttpServletRequest request) {
        ApiResponse<?> res = customerBalanceService.getAllBalances((String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @GetMapping("/getBalanceByCustomer/{customerId}")
    public ResponseEntity<ApiResponse<?>> getBalanceByCustomer(HttpServletRequest request, @PathVariable Long customerId) {
        ApiResponse<?> res = customerBalanceService.getBalance((String) request.getAttribute("tenantID"), customerId);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @PostMapping("/upsertBalance")
    public ResponseEntity<ApiResponse<?>> upsertBalance(HttpServletRequest request, @RequestBody CustomerBalanceUpdateReqDTO dto) {
        ApiResponse<?> res = customerBalanceService.upsertBalance((String) request.getAttribute("tenantID"), dto);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }
}


