package com.elevate.insc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elevate.insc.entity.PurchaseOrderClass;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderClass, String> {
    
    List<PurchaseOrderClass> findByTenantId(String tenantId);
    
    List<PurchaseOrderClass> findByTenantIdAndSupplierId(String tenantId, String supplierId);
    
    List<PurchaseOrderClass> findByTenantIdAndStatus(String tenantId, PurchaseOrderClass.Status status);
    
    Optional<PurchaseOrderClass> findByTenantIdAndId(String tenantId, String id);
    
    boolean existsByTenantIdAndSupplierId(String tenantId, String supplierId);
    
    @Query("SELECT po FROM PurchaseOrderClass po WHERE po.tenantId = :tenantId AND po.status = :status")
    List<PurchaseOrderClass> findByTenantIdAndStatusQuery(@Param("tenantId") String tenantId, @Param("status") PurchaseOrderClass.Status status);
}
