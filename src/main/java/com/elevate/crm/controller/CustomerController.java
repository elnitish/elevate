package com.elevate.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.dto.CustomerReqDTO;
import com.elevate.crm.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/createCustomer") //working
    public ResponseEntity<ApiResponse<?>> createNewCustomer(HttpServletRequest request, @RequestBody CustomerReqDTO dto) {
        ApiResponse<?> res = customerService.createCustomer((String) request.getAttribute("tenantID"), dto);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @GetMapping("/getAllCustomers") //working
    public ResponseEntity<ApiResponse<?>> getAllCustomers(HttpServletRequest request) {
        ApiResponse<?> res = customerService.getCustomers((String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @GetMapping("/getCustomerById/{id}") //working
    public ResponseEntity<ApiResponse<?>> getCustomerById(HttpServletRequest request, @PathVariable Long id) {
        ApiResponse<?> res = customerService.getCustomerById((String) request.getAttribute("tenantID"), id);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @PutMapping("/updateCustomer/{id}") //working
    public ResponseEntity<ApiResponse<?>> updateCustomerById(HttpServletRequest request, @PathVariable Long id, @RequestBody CustomerReqDTO dto) {
        ApiResponse<?> res = customerService.updateCustomer((String) request.getAttribute("tenantID"), id, dto);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }

    @DeleteMapping("/deleteCustomer/{id}") //working
    public ResponseEntity<ApiResponse<?>> deleteCustomerById(HttpServletRequest request, @PathVariable Long id) {
        ApiResponse<?> res = customerService.deleteCustomer((String) request.getAttribute("tenantID"), id);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getCode()));
    }
}


