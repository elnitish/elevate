package com.elevate.fna.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elevate.fna.entity.PaymentClass;

@Repository
public interface PaymentClassRepo extends JpaRepository<PaymentClass, String> {

    List<PaymentClass> findByTenantId(String tenantId);
    Page<PaymentClass> findByTenantId(String tenantId, Pageable pageable);

    List<PaymentClass> findByTenantIdAndInvoiceId(String tenantId, Long invoiceId);
    Page<PaymentClass> findByTenantIdAndInvoiceId(String tenantId, Long invoiceId, Pageable pageable);
    
    List<PaymentClass> findByTenantIdAndPaymentDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);
    
    Optional<PaymentClass> findByTenantIdAndId(String tenantId, String id);
    
    boolean existsByTenantIdAndInvoiceId(String tenantId, Long invoiceId);
    
    @Query("SELECT p FROM PaymentClass p WHERE p.tenantId = :tenantId AND p.invoiceId = :invoiceId")
    List<PaymentClass> findByTenantIdAndInvoiceIdQuery(@Param("tenantId") String tenantId, @Param("invoiceId") Long invoiceId);
    
    @Query("SELECT SUM(p.amount) FROM PaymentClass p WHERE p.tenantId = :tenantId AND p.invoiceId = :invoiceId")
    java.math.BigDecimal getTotalPaymentsByTenantAndInvoice(@Param("tenantId") String tenantId, @Param("invoiceId") Long invoiceId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentClass p WHERE p.tenantId = :tenantId AND p.paymentDate BETWEEN :start AND :end")
    java.math.BigDecimal sumAmountByTenantAndDateBetween(@Param("tenantId") String tenantId, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("SELECT COUNT(p) FROM PaymentClass p WHERE p.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}
