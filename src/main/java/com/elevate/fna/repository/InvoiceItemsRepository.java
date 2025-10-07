package com.elevate.fna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.elevate.fna.entity.InvoiceItemsClass;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<InvoiceItemsClass, String> {
    
    List<InvoiceItemsClass> findByTenantId(String tenantId);
    
    List<InvoiceItemsClass> findByTenantIdAndProductId(String tenantId, String productId);
    
    Optional<InvoiceItemsClass> findByTenantIdAndId(String tenantId, String id);
    
    boolean existsByTenantIdAndInvoiceInvoiceId(String tenantId, Long invoiceId);
    
    boolean existsByTenantIdAndProductId(String tenantId, String productId);
    
    @Query("SELECT ii FROM InvoiceItemsClass ii WHERE ii.tenantId = :tenantId AND ii.invoice.invoiceId = :invoiceId")
    List<InvoiceItemsClass> findByTenantIdAndInvoiceInvoiceId(@Param("tenantId") String tenantId, @Param("invoiceId") Long invoiceId);
    
    @Query("SELECT ii FROM InvoiceItemsClass ii WHERE ii.tenantId = :tenantId AND ii.product.id = :productId")
    List<InvoiceItemsClass> findByTenantIdAndProductProductId(@Param("tenantId") String tenantId, @Param("productId") String productId);
}
