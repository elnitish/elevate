package com.elevate.crm.service;

import java.util.List;
import java.util.stream.Collectors;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.PaymentClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.dto.CustomerLedgerResDTO;
import com.elevate.crm.entity.CustomerLedgerClass;
import com.elevate.crm.repository.CustomerLedgerRepository;

@Service
public class CustomerLedgerService {

    private final CustomerLedgerRepository ledgerRepository;
    private final CustomerBalanceService  customerBalanceService;
    @Autowired
    public CustomerLedgerService(CustomerLedgerRepository ledgerRepository,
                                 CustomerBalanceService  customerBalanceService) {
        this.ledgerRepository = ledgerRepository;
        this.customerBalanceService = customerBalanceService;
    }

    @Transactional
    public ApiResponse<?> addEntryForInvoice(InvoiceClass invoice) {
        CustomerLedgerClass entry = new CustomerLedgerClass();
        entry.setTenantId(invoice.getTenantId());
        entry.setCustomer(invoice.getCustomer());
        entry.setReferenceType(CustomerLedgerClass.ReferenceType.INVOICE);
        entry.setReferenceId(invoice.getInvoiceId());
        entry.setEntryType(CustomerLedgerClass.EntryType.DEBIT);
        entry.setAmount(invoice.getTotalAmount());
        entry.setDescription("Invoice created");
        customerBalanceService.upsertBalanceForInvoice(entry);
        CustomerLedgerClass saved = ledgerRepository.save(entry);

        return new ApiResponse<>("Ledger entry added", 201, new CustomerLedgerResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> addEntryForPayment(PaymentClass payment) {
        CustomerLedgerClass entry = new CustomerLedgerClass();
        entry.setTenantId(payment.getTenantId());
        entry.setCustomer(payment.getCustomer());
        entry.setReferenceType(CustomerLedgerClass.ReferenceType.INVOICE);
        entry.setReferenceId(payment.getInvoiceId());
        entry.setEntryType(CustomerLedgerClass.EntryType.CREDIT);
        entry.setAmount(payment.getAmount());
        entry.setDescription("Payment received");
        customerBalanceService.upsertBalanceForPayment(entry);
        CustomerLedgerClass saved = ledgerRepository.save(entry);

        return new ApiResponse<>("Ledger entry added", 201, new CustomerLedgerResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> addEntryForPaymentReversal(PaymentClass payment) {
        // Create a debit entry to reverse the credit from the original payment
        CustomerLedgerClass entry = new CustomerLedgerClass();
        entry.setTenantId(payment.getTenantId());
        entry.setCustomer(payment.getCustomer());
        entry.setReferenceType(CustomerLedgerClass.ReferenceType.PAYMENT);
        entry.setReferenceId(payment.getInvoiceId());
        entry.setEntryType(CustomerLedgerClass.EntryType.DEBIT);
        entry.setAmount(payment.getAmount());
        entry.setDescription("Payment deleted/reversed");
        customerBalanceService.upsertBalanceForInvoice(entry);
        CustomerLedgerClass saved = ledgerRepository.save(entry);

        return new ApiResponse<>("Payment reversal ledger entry added", 201, new CustomerLedgerResDTO(saved));
    }

    public ApiResponse<?> getAllEntries(String tenantId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        Page<CustomerLedgerClass> entries = ledgerRepository.findByTenantId(tenantId, pageable);
        List<CustomerLedgerResDTO> list = entries.getContent().stream().map(CustomerLedgerResDTO::new).collect(Collectors.toList());
        java.util.Map<String,Object> payload = new java.util.HashMap<>();
        payload.put("content", list);
        payload.put("page", entries.getNumber());
        payload.put("size", entries.getSize());
        payload.put("totalElements", entries.getTotalElements());
        payload.put("totalPages", entries.getTotalPages());
        return new ApiResponse<>("Ledger entries retrieved", 200, payload);
    }

    public ApiResponse<?> getEntriesByCustomer(String tenantId, Long customerId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        Page<CustomerLedgerClass> entries = ledgerRepository.findByTenantIdAndCustomer_Id(tenantId, customerId, pageable);
        List<CustomerLedgerResDTO> list = entries.getContent().stream().map(CustomerLedgerResDTO::new).collect(Collectors.toList());
        java.util.Map<String,Object> payload = new java.util.HashMap<>();
        payload.put("content", list);
        payload.put("page", entries.getNumber());
        payload.put("size", entries.getSize());
        payload.put("totalElements", entries.getTotalElements());
        payload.put("totalPages", entries.getTotalPages());
        return new ApiResponse<>("Customer ledger entries retrieved", 200, payload);
    }
}


