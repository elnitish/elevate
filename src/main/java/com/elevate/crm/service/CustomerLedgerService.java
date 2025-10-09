package com.elevate.crm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.crm.dto.CustomerLedgerReqDTO;
import com.elevate.crm.dto.CustomerLedgerResDTO;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.entity.CustomerLedgerClass;
import com.elevate.crm.repository.CustomerLedgerRepository;
import com.elevate.crm.repository.CustomerRepository;

@Service
public class CustomerLedgerService {

    private final CustomerLedgerRepository ledgerRepository;
    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public CustomerLedgerService(CustomerLedgerRepository ledgerRepository,
                                 CustomerRepository customerRepository,
                                 TenantRepository tenantRepository) {
        this.ledgerRepository = ledgerRepository;
        this.customerRepository = customerRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public ApiResponse<?> addEntry(String tenantId, CustomerLedgerReqDTO dto) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        CustomerClass customer = customerRepository.findByTenantIdAndId(tenantId, dto.getCustomerId())
                .orElse(null);
        if (customer == null) {
            return new ApiResponse<>("Customer not found", 404, null);
        }

        CustomerLedgerClass entry = new CustomerLedgerClass();
        entry.setTenantId(tenantId);
        entry.setCustomer(customer);
        entry.setReferenceType(CustomerLedgerClass.ReferenceType.valueOf(dto.getReferenceType().toUpperCase()));
        entry.setReferenceId(dto.getReferenceId());
        entry.setEntryType(CustomerLedgerClass.EntryType.valueOf(dto.getEntryType().toUpperCase()));
        entry.setAmount(dto.getAmount());
        entry.setDescription(dto.getDescription());

        CustomerLedgerClass saved = ledgerRepository.save(entry);
        return new ApiResponse<>("Ledger entry added", 201, new CustomerLedgerResDTO(saved));
    }

    public ApiResponse<?> getEntriesByTenant(String tenantId) {
        List<CustomerLedgerResDTO> list = ledgerRepository.findByTenantId(tenantId)
                .stream().map(CustomerLedgerResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Ledger entries retrieved", 200, list);
    }

    public ApiResponse<?> getEntriesByCustomer(String tenantId, Long customerId) {
        List<CustomerLedgerResDTO> list = ledgerRepository.findByTenantIdAndCustomerId(tenantId, customerId)
                .stream().map(CustomerLedgerResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Customer ledger entries retrieved", 200, list);
    }
}


