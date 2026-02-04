package com.elevate.fna.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.ExpenseReqDTO;
import com.elevate.fna.dto.ExpenseResDTO;
import com.elevate.fna.service.ExpenseService;

@RestController
@RequestMapping("/finance/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createExpense(HttpServletRequest request, @Valid @RequestBody ExpenseReqDTO expenseReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        String createdBy = (String) request.getAttribute("username");
        
        ExpenseResDTO createdExpense = expenseService.createExpense(tenantId, expenseReqDTO, createdBy);
        ApiResponse<?> response = new ApiResponse<>(
            "Expense created successfully",
            HttpStatus.CREATED.value(),
            createdExpense
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllExpenses(HttpServletRequest request) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<ExpenseResDTO> expenses = expenseService.getAllExpenses(tenantId);
        ApiResponse<?> response = new ApiResponse<>(
            "Expenses retrieved successfully",
            HttpStatus.OK.value(),
            expenses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<?>> getExpenseById(HttpServletRequest request, @PathVariable Long expenseId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        Optional<ExpenseResDTO> expense = expenseService.getExpenseById(tenantId, expenseId);
        if (expense.isPresent()) {
            ApiResponse<?> response = new ApiResponse<>(
                "Expense retrieved successfully",
                HttpStatus.OK.value(),
                expense.get()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Expense not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<?>> getExpensesByStatus(HttpServletRequest request, @PathVariable String status) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<ExpenseResDTO> expenses = expenseService.getExpensesByStatus(tenantId, status);
        ApiResponse<?> response = new ApiResponse<>(
            "Expenses retrieved successfully",
            HttpStatus.OK.value(),
            expenses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<ApiResponse<?>> getExpensesByCategory(HttpServletRequest request, @PathVariable String category) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<ExpenseResDTO> expenses = expenseService.getExpensesByCategory(tenantId, category);
        ApiResponse<?> response = new ApiResponse<>(
            "Expenses retrieved successfully",
            HttpStatus.OK.value(),
            expenses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse<?>> getExpensesByDateRange(
            HttpServletRequest request,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<ExpenseResDTO> expenses = expenseService.getExpensesByDateRange(tenantId, startDate, endDate);
        ApiResponse<?> response = new ApiResponse<>(
            "Expenses retrieved successfully",
            HttpStatus.OK.value(),
            expenses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<?>> updateExpense(
            HttpServletRequest request,
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseReqDTO expenseReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        ExpenseResDTO updatedExpense = expenseService.updateExpense(tenantId, expenseId, expenseReqDTO);
        if (updatedExpense != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Expense updated successfully",
                HttpStatus.OK.value(),
                updatedExpense
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Expense not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<?>> deleteExpense(HttpServletRequest request, @PathVariable Long expenseId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        boolean deleted = expenseService.deleteExpense(tenantId, expenseId);
        if (deleted) {
            ApiResponse<?> response = new ApiResponse<>(
                "Expense deleted successfully",
                HttpStatus.OK.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Expense not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{expenseId}/approve")
    public ResponseEntity<ApiResponse<?>> approveExpense(HttpServletRequest request, @PathVariable Long expenseId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        ExpenseResDTO approvedExpense = expenseService.approveExpense(tenantId, expenseId);
        if (approvedExpense != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Expense approved successfully",
                HttpStatus.OK.value(),
                approvedExpense
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Expense not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{expenseId}/reject")
    public ResponseEntity<ApiResponse<?>> rejectExpense(HttpServletRequest request, @PathVariable Long expenseId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        ExpenseResDTO rejectedExpense = expenseService.rejectExpense(tenantId, expenseId);
        if (rejectedExpense != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Expense rejected successfully",
                HttpStatus.OK.value(),
                rejectedExpense
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Expense not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
