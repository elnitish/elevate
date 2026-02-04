package com.elevate.fna.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.fna.dto.ExpenseReqDTO;
import com.elevate.fna.dto.ExpenseResDTO;
import com.elevate.fna.entity.ExpenseClass;
import com.elevate.fna.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResDTO createExpense(String tenantId, ExpenseReqDTO expenseReqDTO, String createdBy) {
        ExpenseClass expense = new ExpenseClass();
        expense.setTenantId(tenantId);
        expense.setAmount(expenseReqDTO.getAmount());
        expense.setCategory(expenseReqDTO.getCategory());
        expense.setDescription(expenseReqDTO.getDescription());
        expense.setExpenseDate(expenseReqDTO.getExpenseDate());
        expense.setReferenceNumber(expenseReqDTO.getReferenceNumber());
        expense.setCreatedBy(createdBy);
        
        if (expenseReqDTO.getStatus() != null) {
            try {
                expense.setStatus(ExpenseClass.Status.valueOf(expenseReqDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                expense.setStatus(ExpenseClass.Status.PENDING);
            }
        } else {
            expense.setStatus(ExpenseClass.Status.PENDING);
        }

        ExpenseClass savedExpense = expenseRepository.save(expense);
        return convertToResDTO(savedExpense);
    }

    public List<ExpenseResDTO> getAllExpenses(String tenantId) {
        return expenseRepository.findByTenantId(tenantId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseResDTO> getExpensesByStatus(String tenantId, String status) {
        return expenseRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseResDTO> getExpensesByCategory(String tenantId, String category) {
        return expenseRepository.findByTenantIdAndCategory(tenantId, category).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<ExpenseResDTO> getExpensesByDateRange(String tenantId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByTenantIdAndExpenseDateBetween(tenantId, startDate, endDate).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public Optional<ExpenseResDTO> getExpenseById(String tenantId, Long expenseId) {
        Optional<ExpenseClass> expense = expenseRepository.findById(expenseId);
        if (expense.isPresent() && expense.get().getTenantId().equals(tenantId)) {
            return expense.map(this::convertToResDTO);
        }
        return Optional.empty();
    }

    @Transactional
    public ExpenseResDTO updateExpense(String tenantId, Long expenseId, ExpenseReqDTO expenseReqDTO) {
        Optional<ExpenseClass> expense = expenseRepository.findById(expenseId);
        if (expense.isPresent() && expense.get().getTenantId().equals(tenantId)) {
            ExpenseClass expenseClass = expense.get();
            expenseClass.setAmount(expenseReqDTO.getAmount());
            expenseClass.setCategory(expenseReqDTO.getCategory());
            expenseClass.setDescription(expenseReqDTO.getDescription());
            expenseClass.setExpenseDate(expenseReqDTO.getExpenseDate());
            expenseClass.setReferenceNumber(expenseReqDTO.getReferenceNumber());
            
            if (expenseReqDTO.getStatus() != null) {
                try {
                    expenseClass.setStatus(ExpenseClass.Status.valueOf(expenseReqDTO.getStatus().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Keep existing status
                }
            }

            ExpenseClass updatedExpense = expenseRepository.save(expenseClass);
            return convertToResDTO(updatedExpense);
        }
        return null;
    }

    @Transactional
    public boolean deleteExpense(String tenantId, Long expenseId) {
        Optional<ExpenseClass> expense = expenseRepository.findById(expenseId);
        if (expense.isPresent() && expense.get().getTenantId().equals(tenantId)) {
            expenseRepository.deleteById(expenseId);
            return true;
        }
        return false;
    }

    @Transactional
    public ExpenseResDTO approveExpense(String tenantId, Long expenseId) {
        Optional<ExpenseClass> expense = expenseRepository.findById(expenseId);
        if (expense.isPresent() && expense.get().getTenantId().equals(tenantId)) {
            ExpenseClass expenseClass = expense.get();
            expenseClass.setStatus(ExpenseClass.Status.APPROVED);
            ExpenseClass updatedExpense = expenseRepository.save(expenseClass);
            return convertToResDTO(updatedExpense);
        }
        return null;
    }

    @Transactional
    public ExpenseResDTO rejectExpense(String tenantId, Long expenseId) {
        Optional<ExpenseClass> expense = expenseRepository.findById(expenseId);
        if (expense.isPresent() && expense.get().getTenantId().equals(tenantId)) {
            ExpenseClass expenseClass = expense.get();
            expenseClass.setStatus(ExpenseClass.Status.REJECTED);
            ExpenseClass updatedExpense = expenseRepository.save(expenseClass);
            return convertToResDTO(updatedExpense);
        }
        return null;
    }

    private ExpenseResDTO convertToResDTO(ExpenseClass expense) {
        ExpenseResDTO resDTO = new ExpenseResDTO();
        resDTO.setExpenseId(expense.getExpenseId());
        resDTO.setTenantId(expense.getTenantId());
        resDTO.setAmount(expense.getAmount());
        resDTO.setCategory(expense.getCategory());
        resDTO.setDescription(expense.getDescription());
        resDTO.setExpenseDate(expense.getExpenseDate());
        resDTO.setStatus(expense.getStatus().toString());
        resDTO.setReferenceNumber(expense.getReferenceNumber());
        resDTO.setCreatedBy(expense.getCreatedBy());
        resDTO.setCreatedAt(expense.getCreatedAt());
        resDTO.setUpdatedAt(expense.getUpdatedAt());
        return resDTO;
    }

}
