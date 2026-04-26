package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceClassRepo extends JpaRepository<InvoiceClass, Long> {

    List<InvoiceClass> findByTenantId(String tenantId);
    Page<InvoiceClass> findByTenantId(String tenantId, Pageable pageable);

    List<InvoiceClass> findByTenantIdAndStatus(String tenantId, InvoiceClass.Status status);
    Page<InvoiceClass> findByTenantIdAndStatus(String tenantId, InvoiceClass.Status status, Pageable pageable);

    List<InvoiceClass> findByTenantIdAndDate(String tenantId, LocalDate date);
    List<InvoiceClass> findByTenantIdAndDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT i FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.status = :status")
    List<InvoiceClass> findByTenantIdAndStatusString(@Param("tenantId") String tenantId, @Param("status") String status);

    @Query("SELECT i FROM InvoiceClass i WHERE i.tenantId = :tenantId AND CAST(i.status AS string) = :status")
    Page<InvoiceClass> findByTenantIdAndStatusStringPaged(@Param("tenantId") String tenantId, @Param("status") String status, Pageable pageable);

    boolean existsByTenantIdAndInvoiceId(String tenantId, Long invoiceId);

    Optional<InvoiceClass> findByTenantIdAndInvoiceId(String tenantId, Long invoiceId);

    @Query("SELECT i FROM InvoiceClass i WHERE i.status IN :statuses AND i.dueDate < :today")
    List<InvoiceClass> findOverdueInvoices(@Param("statuses") List<InvoiceClass.Status> statuses, @Param("today") java.time.LocalDate today);

    // --- Aggregation queries for reports ---

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.date BETWEEN :start AND :end")
    java.math.BigDecimal sumTotalAmountByTenantAndDateBetween(@Param("tenantId") String tenantId, @Param("start") java.time.LocalDate start, @Param("end") java.time.LocalDate end);

    @Query("SELECT COALESCE(SUM(i.remainingAmount), 0) FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.status NOT IN ('PAID','CANCELLED')")
    java.math.BigDecimal sumOutstandingReceivables(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(i) FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.date BETWEEN :start AND :end")
    long countByTenantAndDateBetween(@Param("tenantId") String tenantId, @Param("start") java.time.LocalDate start, @Param("end") java.time.LocalDate end);

    @Query("SELECT COUNT(i) FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.status = :status AND i.date BETWEEN :start AND :end")
    long countByTenantAndStatusAndDateBetween(@Param("tenantId") String tenantId, @Param("status") InvoiceClass.Status status, @Param("start") java.time.LocalDate start, @Param("end") java.time.LocalDate end);

    @Query("SELECT COUNT(i) FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") InvoiceClass.Status status);
}
