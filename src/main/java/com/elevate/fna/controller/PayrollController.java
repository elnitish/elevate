package com.elevate.fna.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.elevate.fna.dto.PayrollReqDTO;
import com.elevate.fna.dto.PayrollResDTO;
import com.elevate.fna.service.PayrollService;

@RestController
@RequestMapping("/finance/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPayroll(HttpServletRequest request, @Valid @RequestBody PayrollReqDTO payrollReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            PayrollResDTO createdPayroll = payrollService.createPayroll(tenantId, payrollReqDTO);
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll created successfully",
                HttpStatus.CREATED.value(),
                createdPayroll
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPayrolls(HttpServletRequest request) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PayrollResDTO> payrolls = payrollService.getAllPayrolls(tenantId);
        ApiResponse<?> response = new ApiResponse<>(
            "Payrolls retrieved successfully",
            HttpStatus.OK.value(),
            payrolls
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{payrollId}")
    public ResponseEntity<ApiResponse<?>> getPayrollById(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        Optional<PayrollResDTO> payroll = payrollService.getPayrollById(tenantId, payrollId);
        if (payroll.isPresent()) {
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll retrieved successfully",
                HttpStatus.OK.value(),
                payroll.get()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Payroll not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<?>> getPayrollsByEmployee(HttpServletRequest request, @PathVariable Long employeeId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PayrollResDTO> payrolls = payrollService.getPayrollsByEmployee(tenantId, employeeId);
        ApiResponse<?> response = new ApiResponse<>(
            "Payrolls retrieved successfully",
            HttpStatus.OK.value(),
            payrolls
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<?>> getPayrollsByStatus(HttpServletRequest request, @PathVariable String status) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PayrollResDTO> payrolls = payrollService.getPayrollsByStatus(tenantId, status);
        ApiResponse<?> response = new ApiResponse<>(
            "Payrolls retrieved successfully",
            HttpStatus.OK.value(),
            payrolls
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-month/{yearMonth}")
    public ResponseEntity<ApiResponse<?>> getPayrollsByMonth(HttpServletRequest request, @PathVariable String yearMonth) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PayrollResDTO> payrolls = payrollService.getPayrollsByMonth(tenantId, yearMonth);
        ApiResponse<?> response = new ApiResponse<>(
            "Payrolls retrieved successfully",
            HttpStatus.OK.value(),
            payrolls
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{payrollId}")
    public ResponseEntity<ApiResponse<?>> updatePayroll(
            HttpServletRequest request,
            @PathVariable Long payrollId,
            @Valid @RequestBody PayrollReqDTO payrollReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            PayrollResDTO updatedPayroll = payrollService.updatePayroll(tenantId, payrollId, payrollReqDTO);
            if (updatedPayroll != null) {
                ApiResponse<?> response = new ApiResponse<>(
                    "Payroll updated successfully",
                    HttpStatus.OK.value(),
                    updatedPayroll
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll not found",
                HttpStatus.NOT_FOUND.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{payrollId}")
    public ResponseEntity<ApiResponse<?>> deletePayroll(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            boolean deleted = payrollService.deletePayroll(tenantId, payrollId);
            if (deleted) {
                ApiResponse<?> response = new ApiResponse<>(
                    "Payroll deleted successfully",
                    HttpStatus.OK.value(),
                    null
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll not found",
                HttpStatus.NOT_FOUND.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{payrollId}/submit")
    public ResponseEntity<ApiResponse<?>> submitPayroll(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        PayrollResDTO submittedPayroll = payrollService.submitPayroll(tenantId, payrollId);
        if (submittedPayroll != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll submitted successfully",
                HttpStatus.OK.value(),
                submittedPayroll
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Payroll not found or cannot be submitted",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{payrollId}/approve")
    public ResponseEntity<ApiResponse<?>> approvePayroll(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        PayrollResDTO approvedPayroll = payrollService.approvePayroll(tenantId, payrollId);
        if (approvedPayroll != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll approved successfully",
                HttpStatus.OK.value(),
                approvedPayroll
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Payroll not found or cannot be approved",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{payrollId}/process")
    public ResponseEntity<ApiResponse<?>> processPayroll(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        PayrollResDTO processedPayroll = payrollService.processPayroll(tenantId, payrollId);
        if (processedPayroll != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll processed successfully",
                HttpStatus.OK.value(),
                processedPayroll
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Payroll not found or cannot be processed",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{payrollId}/pay")
    public ResponseEntity<ApiResponse<?>> payPayroll(HttpServletRequest request, @PathVariable Long payrollId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        PayrollResDTO paidPayroll = payrollService.payPayroll(tenantId, payrollId);
        if (paidPayroll != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Payroll paid successfully",
                HttpStatus.OK.value(),
                paidPayroll
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Payroll not found or cannot be paid",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
