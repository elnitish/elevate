package com.elevate.fna.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.service.SessionService;
import com.elevate.fna.dto.PaymentClassReqDTO;
import com.elevate.fna.entity.CustomerClass;
import com.elevate.fna.service.CustomerService;
import com.elevate.fna.service.InvoiceService;
import com.elevate.fna.dto.InvoiceReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;

    }

    @PostMapping("/createInvoice") //working
    public ResponseEntity<?> createInvoice(HttpServletRequest request, @RequestBody InvoiceReqDTO invoiceReqDTO) {
        ApiResponse<?> response = invoiceService.createNewInvoice((String) request.getAttribute("tenantID"), invoiceReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/getAllInvoices") //woking // "http://localhost:8080/getAllInvoices" //"http://localhost:8080/getAllInvoices?status=PAID"
    public ResponseEntity<?> getAllInvoices(HttpServletRequest request, @RequestParam(required = false) String status) {
        ApiResponse<?> response;
        if (status == null) {
            response = invoiceService.returnAllInvoices((String) request.getAttribute("tenantID"));
        } else {
            response = invoiceService.returnInvoicesWithStatus((String) request.getAttribute("tenantID"), status);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/invoices/{id}/{status}") //working
    public ResponseEntity<?> updateInvoiceStatus(HttpServletRequest request, @PathVariable Long id, @PathVariable String status) {
        ApiResponse<?> response = invoiceService.updateInvoiceStatus((String) request.getAttribute("tenantID"), id, status);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/getInvoiceById") //woking // "http://localhost:8080/getAllInvoices" //"http://localhost:8080/getAllInvoices?status=PAID"
    public ResponseEntity<?> getInvoiceById(HttpServletRequest request, @RequestParam String id) {
        ApiResponse<?> response = invoiceService.returnInvoiceWithID((String) request.getAttribute("tenantID"),id);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}