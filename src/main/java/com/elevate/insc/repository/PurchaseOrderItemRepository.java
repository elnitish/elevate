package com.elevate.insc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elevate.insc.entity.PurchaseOrderItemClass;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItemClass, String> {
    
    List<PurchaseOrderItemClass> findByTenantId(String tenantId);
    
    List<PurchaseOrderItemClass> findByTenantIdAndPurchaseOrderId(String tenantId, String purchaseOrderId);
    
    List<PurchaseOrderItemClass> findByTenantIdAndProductId(String tenantId, String productId);
    
    Optional<PurchaseOrderItemClass> findByTenantIdAndId(String tenantId, String id);
    
    boolean existsByTenantIdAndPurchaseOrderId(String tenantId, String purchaseOrderId);
    
    @Query("SELECT poi FROM PurchaseOrderItemClass poi WHERE poi.tenantId = :tenantId AND poi.purchaseOrder.id = :purchaseOrderId")
    List<PurchaseOrderItemClass> findByTenantIdAndPurchaseOrderIdQuery(@Param("tenantId") String tenantId, @Param("purchaseOrderId") String purchaseOrderId);
}
