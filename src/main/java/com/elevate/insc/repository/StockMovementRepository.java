package com.elevate.insc.repository;

import com.elevate.insc.entity.StockMovementClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementClass, String> {
    
    // Find movements by tenant and product
    List<StockMovementClass> findByTenantIdAndProductId(String tenantId, String productId);
    
    // Find movements by tenant
    List<StockMovementClass> findByTenantId(String tenantId);
    
    // Find movements by purchase order
    List<StockMovementClass> findByTenantIdAndPurchaseOrderId(String tenantId, String purchaseOrderId);
    
    // Find movements by invoice
    List<StockMovementClass> findByTenantIdAndInvoiceId(String tenantId, String invoiceId);
    
    // Find movements by type (IN/OUT)
    List<StockMovementClass> findByTenantIdAndType(String tenantId, StockMovementClass.Type type);
    
    // Find movements by date range
    List<StockMovementClass> findByTenantIdAndDateBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Get total stock movements for a product (IN - OUT)
    @Query("SELECT COALESCE(SUM(CASE WHEN s.type = 'IN' THEN s.quantity ELSE -s.quantity END), 0) " +
           "FROM StockMovementClass s WHERE s.tenantId = :tenantId AND s.productId = :productId")
    Integer getNetStockMovementByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);
    
    // Get total IN movements for a product
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovementClass s " +
           "WHERE s.tenantId = :tenantId AND s.productId = :productId AND s.type = 'IN'")
    Integer getTotalInMovementsByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);
    
    // Get total OUT movements for a product
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovementClass s " +
           "WHERE s.tenantId = :tenantId AND s.productId = :productId AND s.type = 'OUT'")
    Integer getTotalOutMovementsByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);
}
