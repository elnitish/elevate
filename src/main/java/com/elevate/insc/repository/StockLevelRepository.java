package com.elevate.insc.repository;

import com.elevate.insc.entity.StockLevelClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevelClass, String> {
    
    // Find stock level by tenant and product
    Optional<StockLevelClass> findByTenantIdAndProductId(String tenantId, String productId);
    
    // Find all stock levels for a tenant
    List<StockLevelClass> findByTenantId(String tenantId);
    
    // Find all stock levels for a product
    List<StockLevelClass> findByProductId(String productId);
    
    // Check if stock level exists for a product
    boolean existsByTenantIdAndProductId(String tenantId, String productId);
    
    // Get total stock quantity for a product
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockLevelClass s WHERE s.tenantId = :tenantId AND s.productId = :productId")
    Integer getTotalStockByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);
    
    // Find products with low stock (quantity <= threshold)
    @Query("SELECT s FROM StockLevelClass s WHERE s.tenantId = :tenantId AND s.quantity <= :threshold")
    List<StockLevelClass> findLowStockProducts(@Param("tenantId") String tenantId, @Param("threshold") Integer threshold);
}
