package com.elevate.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elevate.crm.entity.CustomerLedgerClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.dto.CustomerBalanceResDTO;
import com.elevate.crm.entity.CustomerBalanceClass;
import com.elevate.crm.repository.CustomerBalanceRepository;

@Service
public class CustomerBalanceService {

    private final CustomerBalanceRepository balanceRepository;

    @Autowired
    public CustomerBalanceService(CustomerBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public ApiResponse<?> getBalancesOfAllCustomers(String tenantId, Integer page, Integer size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        org.springframework.data.domain.Page<com.elevate.crm.entity.CustomerBalanceClass> balances = balanceRepository.findByIdTenantId(tenantId, pageable);
        List<CustomerBalanceResDTO> list = balances.getContent().stream().map(CustomerBalanceResDTO::new).collect(Collectors.toList());
        java.util.Map<String,Object> payload = new java.util.HashMap<>();
        payload.put("content", list);
        payload.put("page", balances.getNumber());
        payload.put("size", balances.getSize());
        payload.put("totalElements", balances.getTotalElements());
        payload.put("totalPages", balances.getTotalPages());
        return new ApiResponse<>("Customer balances retrieved", 200, payload);
    }

    public ApiResponse<?> getBalanceOfCustomer(String tenantId, Long customerId) {
        Optional<CustomerBalanceClass> opt = balanceRepository.findByIdTenantIdAndIdCustomerId(tenantId, customerId);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Customer balance not found", 404, null);
        }
        return new ApiResponse<>("Customer balance retrieved", 200, new CustomerBalanceResDTO(opt.get()));
    }

    @Transactional
    public ApiResponse<?> upsertBalanceForInvoice(CustomerLedgerClass entry) {
        Long customerId = entry.getCustomer().getId();
        Optional<CustomerBalanceClass> entity  = balanceRepository.findByIdTenantIdAndIdCustomerId(entry.getTenantId(), customerId);

        CustomerBalanceClass balance = entity.orElseGet(() -> new CustomerBalanceClass(entry.getCustomer()));

        if (entry.getEntryType().equals(CustomerLedgerClass.EntryType.DEBIT)) {
            balance.setTotalDebit(balance.getTotalDebit().add(entry.getAmount()));
        } else {
            balance.setTotalCredit(balance.getTotalCredit().add(entry.getAmount()));
        }

        CustomerBalanceClass saved = balanceRepository.save(balance);
        return new ApiResponse<>("Customer balance saved", 201, new CustomerBalanceResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> upsertBalanceForPayment(CustomerLedgerClass entry) {
        Long customerId = entry.getCustomer().getId();
        Optional<CustomerBalanceClass> entity  = balanceRepository.findByIdTenantIdAndIdCustomerId(entry.getTenantId(), customerId);

        CustomerBalanceClass balance = entity.orElseGet(() -> new CustomerBalanceClass(entry.getCustomer()));

        if (entry.getEntryType().equals(CustomerLedgerClass.EntryType.DEBIT)) {
            balance.setTotalDebit(balance.getTotalDebit().add(entry.getAmount()));
        } else {
            balance.setTotalCredit(balance.getTotalCredit().add(entry.getAmount()));
        }

        CustomerBalanceClass saved = balanceRepository.save(balance);
        return new ApiResponse<>("Customer balance saved", 201, new CustomerBalanceResDTO(saved));
    }
}


