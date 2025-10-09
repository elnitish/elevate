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

    @GetMapping("/getAllBalances")//working
    public ResponseEntity<ApiResponse<?>> getAllBalances(HttpServletRequest request,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer size) {
        ApiResponse<?> res = customerBalanceService.getBalancesOfAllCustomers((String) request.getAttribute("tenantID"), page, size);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @GetMapping("/getBalanceByCustomer/{customerId}") //working
    public ResponseEntity<ApiResponse<?>> getBalanceByCustomer(HttpServletRequest request, @PathVariable Long customerId) {
        ApiResponse<?> res = customerBalanceService.getBalanceOfCustomer((String) request.getAttribute("tenantID"), customerId);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

}


