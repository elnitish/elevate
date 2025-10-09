package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceClass;
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
    List<InvoiceClass> findByTenantIdAndStatus(String tenantId, InvoiceClass.Status status);
    List<InvoiceClass> findByTenantIdAndDate(String tenantId, LocalDate date);
    
    @Query("SELECT i FROM InvoiceClass i WHERE i.tenantId = :tenantId AND i.status = :status")
    List<InvoiceClass> findByTenantIdAndStatusString(@Param("tenantId") String tenantId, @Param("status") String status);
    
    boolean existsByTenantIdAndInvoiceId(String tenantId, Long invoiceId);

    Optional<InvoiceClass> findByTenantIdAndInvoiceId(String tenantId, Long invoiceId);
}
