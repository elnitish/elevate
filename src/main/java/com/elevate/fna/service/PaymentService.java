package com.elevate.fna.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elevate.crm.service.CustomerBalanceService;
import com.elevate.crm.service.CustomerLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.fna.dto.PaymentReqDTO;
import com.elevate.fna.dto.PaymentResDTO;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.PaymentClass;
import com.elevate.fna.repository.InvoiceClassRepo;
import com.elevate.fna.repository.PaymentClassRepo;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.repository.CustomerRepository;

@Service
public class PaymentService {
    
    private final PaymentClassRepo paymentClassRepo;
    private final InvoiceClassRepo invoiceClassRepo;
    private final TenantRepository tenantRepository;
    private final CustomerRepository customerRepository;
    private final CustomerLedgerService  customerLedgerService;
    
    @Autowired
    public PaymentService(PaymentClassRepo paymentClassRepo, 
                         InvoiceClassRepo invoiceClassRepo,
                         TenantRepository tenantRepository,
                         CustomerRepository customerRepository,
                          CustomerLedgerService customerLedgerService) {
        this.paymentClassRepo = paymentClassRepo;
        this.invoiceClassRepo = invoiceClassRepo;
        this.tenantRepository = tenantRepository;
        this.customerRepository = customerRepository;
        this.customerLedgerService = customerLedgerService;
    }
    
    @Transactional
    public ApiResponse<?> createPayment(String tenantId, PaymentReqDTO paymentReqDTO) {
        // Validate invoice exists and belongs to tenant
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantId, paymentReqDTO.getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        
        InvoiceClass invoice = invoiceOpt.get();

        java.util.Optional<CustomerClass> customerOpt = customerRepository.findByTenantIdAndId(tenantId, paymentReqDTO.getCustomerId());
        if (customerOpt.isEmpty() || invoice.getCustomer() == null || !invoice.getCustomer().getId().equals(customerOpt.get().getId())) {
            return new ApiResponse<>("Customer does not match the invoice", 400, null);
        }
        try {
            PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>("Invalid payment method. Must be CASH, CARD, BANK_TRANSFER, or UPI", 400, null);
        }
        String paymentId = UUID.randomUUID().toString();
        
        PaymentClass newPayment = new PaymentClass(
            paymentId,
            tenantId,
            paymentReqDTO.getInvoiceId(),
            invoice.getCustomer(),
            paymentReqDTO.getAmount(),
            PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase()),
            paymentReqDTO.getTransactionRef()
        );
        invoice.setRemainingAmount(invoice.getRemainingAmount().subtract(newPayment.getAmount()));
        PaymentClass savedPayment = paymentClassRepo.save(newPayment);
        customerLedgerService.addEntryForPayment(newPayment);
        PaymentResDTO responseDTO = new PaymentResDTO(savedPayment);
        return new ApiResponse<>("Payment created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getPaymentsByInvoice(String tenantId, Long invoiceId) {
        List<PaymentClass> payments = paymentClassRepo.findByTenantIdAndInvoiceId(tenantId, invoiceId);
        List<PaymentResDTO> paymentDTOs = payments.stream()
                .map(PaymentResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Payments retrieved successfully", 200, paymentDTOs);
    }
    
    public ApiResponse<?> getPaymentById(String tenantId, String paymentId) {
        Optional<PaymentClass> paymentOpt = paymentClassRepo.findByTenantIdAndId(tenantId, paymentId);
        if (paymentOpt.isEmpty()) {
            return new ApiResponse<>("Payment not found", 404, null);
        }
        
        PaymentResDTO responseDTO = new PaymentResDTO(paymentOpt.get());
        return new ApiResponse<>("Payment retrieved successfully", 200, responseDTO);
    }
    
    public ApiResponse<?> returnAllPayments(String tenantId) {
        List<PaymentClass> payments = paymentClassRepo.findByTenantId(tenantId);
        List<PaymentResDTO> paymentDTOs = payments.stream()
                .map(PaymentResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Payments retrieved successfully", 200, paymentDTOs);
    }
    
    @Transactional
    public ApiResponse<?> deletePayment(String tenantId, String paymentId) {
        Optional<PaymentClass> paymentOpt = paymentClassRepo.findByTenantIdAndId(tenantId, paymentId);
        if (paymentOpt.isEmpty()) {
            return new ApiResponse<>("Payment not found", 404, null);
        }
        PaymentClass paymentClass = paymentOpt.get();
        
        // Update invoice remaining amount
        Optional<InvoiceClass> invoice = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantId, paymentClass.getInvoiceId());
        if (invoice.isPresent()) {
            InvoiceClass invoiceClass = invoice.get();
            invoiceClass.setRemainingAmount(invoiceClass.getRemainingAmount().add(paymentClass.getAmount()));
            invoiceClassRepo.save(invoiceClass);
        }
        
        // Reverse customer ledger entry (add a debit entry to reverse the credit)
        customerLedgerService.addEntryForPaymentReversal(paymentClass);
        
        // Delete the payment
        paymentClassRepo.deleteById(paymentId);

        return new ApiResponse<>("Payment deleted successfully", 200, null);
    }
    
    public ApiResponse<?> getPaymentSummary(String tenantId, Long invoiceId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Get invoice
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findByTenantIdAndInvoiceId(tenantId, invoiceId);
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        
        InvoiceClass invoice = invoiceOpt.get();
        
        // Get total payments
        BigDecimal totalPaid = paymentClassRepo.getTotalPaymentsByTenantAndInvoice(tenantId, invoiceId);
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        
        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(totalPaid);
        
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("invoiceId", invoice.getInvoiceId());
        summary.put("totalAmount", invoice.getTotalAmount());
        summary.put("totalPaid", totalPaid);
        summary.put("remainingAmount", remainingAmount);
        summary.put("isFullyPaid", remainingAmount.compareTo(BigDecimal.ZERO) <= 0);
        
        return new ApiResponse<>("Payment summary retrieved successfully", 200, summary);
    }
}
