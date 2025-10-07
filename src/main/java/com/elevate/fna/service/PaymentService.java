package com.elevate.fna.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

@Service
public class PaymentService {
    
    private final PaymentClassRepo paymentClassRepo;
    private final InvoiceClassRepo invoiceClassRepo;
    private final TenantRepository tenantRepository;
    
    @Autowired
    public PaymentService(PaymentClassRepo paymentClassRepo, 
                         InvoiceClassRepo invoiceClassRepo,
                         TenantRepository tenantRepository) {
        this.paymentClassRepo = paymentClassRepo;
        this.invoiceClassRepo = invoiceClassRepo;
        this.tenantRepository = tenantRepository;
    }
    
    @Transactional
    public ApiResponse<?> createPayment(PaymentReqDTO paymentReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(paymentReqDTO.getTenantId())) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Validate invoice exists and belongs to tenant
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findById(paymentReqDTO.getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        
        InvoiceClass invoice = invoiceOpt.get();
        if (!invoice.getTenantId().equals(paymentReqDTO.getTenantId())) {
            return new ApiResponse<>("Invoice does not belong to this tenant", 403, null);
        }
        
        // Validate payment method
        try {
            PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>("Invalid payment method. Must be CASH, CARD, BANK_TRANSFER, or UPI", 400, null);
        }
        
        // Check if payment amount exceeds remaining amount
        BigDecimal totalPaid = paymentClassRepo.getTotalPaymentsByTenantAndInvoice(
            paymentReqDTO.getTenantId(), paymentReqDTO.getInvoiceId());
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        
        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(totalPaid);
        if (paymentReqDTO.getAmount().compareTo(remainingAmount) > 0) {
            return new ApiResponse<>("Payment amount exceeds remaining amount. Remaining: " + remainingAmount, 400, null);
        }
        
        // Generate UUID for payment
        String paymentId = UUID.randomUUID().toString();
        
        // Create payment entity
        PaymentClass newPayment = new PaymentClass(
            paymentId,
            paymentReqDTO.getTenantId(),
            paymentReqDTO.getInvoiceId(),
            paymentReqDTO.getAmount(),
            PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase()),
            paymentReqDTO.getTransactionRef()
        );
        
        PaymentClass savedPayment = paymentClassRepo.save(newPayment);
        PaymentResDTO responseDTO = new PaymentResDTO(savedPayment);
        
        return new ApiResponse<>("Payment created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getPaymentsByInvoice(String tenantId, Long invoiceId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<PaymentClass> payments = paymentClassRepo.findByTenantIdAndInvoiceId(tenantId, invoiceId);
        List<PaymentResDTO> paymentDTOs = payments.stream()
                .map(PaymentResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Payments retrieved successfully", 200, paymentDTOs);
    }
    
    public ApiResponse<?> getPaymentById(String tenantId, String paymentId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<PaymentClass> paymentOpt = paymentClassRepo.findByTenantIdAndId(tenantId, paymentId);
        if (paymentOpt.isEmpty()) {
            return new ApiResponse<>("Payment not found", 404, null);
        }
        
        PaymentResDTO responseDTO = new PaymentResDTO(paymentOpt.get());
        return new ApiResponse<>("Payment retrieved successfully", 200, responseDTO);
    }
    
    public ApiResponse<?> getAllPaymentsByTenant(String tenantId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<PaymentClass> payments = paymentClassRepo.findByTenantId(tenantId);
        List<PaymentResDTO> paymentDTOs = payments.stream()
                .map(PaymentResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Payments retrieved successfully", 200, paymentDTOs);
    }
    
    @Transactional
    public ApiResponse<?> updatePayment(String tenantId, String paymentId, PaymentReqDTO paymentReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<PaymentClass> paymentOpt = paymentClassRepo.findByTenantIdAndId(tenantId, paymentId);
        if (paymentOpt.isEmpty()) {
            return new ApiResponse<>("Payment not found", 404, null);
        }
        
        PaymentClass payment = paymentOpt.get();
        
        // Validate payment method
        try {
            PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>("Invalid payment method. Must be CASH, CARD, BANK_TRANSFER, or UPI", 400, null);
        }
        
        // Update fields
        payment.setAmount(paymentReqDTO.getAmount());
        payment.setMethod(PaymentClass.Method.valueOf(paymentReqDTO.getMethod().toUpperCase()));
        payment.setTransactionRef(paymentReqDTO.getTransactionRef());
        
        PaymentClass updatedPayment = paymentClassRepo.save(payment);
        PaymentResDTO responseDTO = new PaymentResDTO(updatedPayment);
        
        return new ApiResponse<>("Payment updated successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> deletePayment(String tenantId, String paymentId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<PaymentClass> paymentOpt = paymentClassRepo.findByTenantIdAndId(tenantId, paymentId);
        if (paymentOpt.isEmpty()) {
            return new ApiResponse<>("Payment not found", 404, null);
        }
        
        paymentClassRepo.deleteById(paymentId);
        return new ApiResponse<>("Payment deleted successfully", 200, null);
    }
    
    public ApiResponse<?> getPaymentSummary(String tenantId, Long invoiceId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Get invoice
        Optional<InvoiceClass> invoiceOpt = invoiceClassRepo.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            return new ApiResponse<>("Invoice not found", 404, null);
        }
        
        InvoiceClass invoice = invoiceOpt.get();
        if (!invoice.getTenantId().equals(tenantId)) {
            return new ApiResponse<>("Invoice does not belong to this tenant", 403, null);
        }
        
        // Get total payments
        BigDecimal totalPaid = paymentClassRepo.getTotalPaymentsByTenantAndInvoice(tenantId, invoiceId);
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }
        
        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(totalPaid);
        
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("invoiceId", invoiceId);
        summary.put("totalAmount", invoice.getTotalAmount());
        summary.put("totalPaid", totalPaid);
        summary.put("remainingAmount", remainingAmount);
        summary.put("isFullyPaid", remainingAmount.compareTo(BigDecimal.ZERO) <= 0);
        
        return new ApiResponse<>("Payment summary retrieved successfully", 200, summary);
    }
}
