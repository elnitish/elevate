package com.elevate.insc.repository;

import com.elevate.insc.entity.StockLevelClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevelClass, String> {

    Optional<StockLevelClass> findByTenantIdAndProductId(String tenantId, String productId);

    List<StockLevelClass> findByTenantId(String tenantId);

    @Query("SELECT sl FROM StockLevelClass sl JOIN FETCH sl.product WHERE sl.tenantId = :tenantId")
    Page<StockLevelClass> findByTenantIdWithProduct(@Param("tenantId") String tenantId, Pageable pageable);

    @Query(value = "SELECT sl FROM StockLevelClass sl JOIN FETCH sl.product WHERE sl.tenantId = :tenantId",
           countQuery = "SELECT COUNT(sl) FROM StockLevelClass sl WHERE sl.tenantId = :tenantId")
    Page<StockLevelClass> findByTenantIdWithProductPaged(@Param("tenantId") String tenantId, Pageable pageable);

    List<StockLevelClass> findByProductId(String productId);

    boolean existsByTenantIdAndProductId(String tenantId, String productId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockLevelClass s WHERE s.tenantId = :tenantId AND s.productId = :productId")
    Integer getTotalStockByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);

    @Query("SELECT s FROM StockLevelClass s JOIN FETCH s.product WHERE s.tenantId = :tenantId AND s.quantity <= :threshold")
    List<StockLevelClass> findLowStockProducts(@Param("tenantId") String tenantId, @Param("threshold") Integer threshold);

    @Query(value = "SELECT s FROM StockLevelClass s JOIN FETCH s.product WHERE s.tenantId = :tenantId AND s.quantity <= :threshold",
           countQuery = "SELECT COUNT(s) FROM StockLevelClass s WHERE s.tenantId = :tenantId AND s.quantity <= :threshold")
    Page<StockLevelClass> findLowStockProducts(@Param("tenantId") String tenantId, @Param("threshold") Integer threshold, Pageable pageable);

    // Warehouse-specific queries
    Optional<StockLevelClass> findByTenantIdAndProductIdAndWarehouseId(String tenantId, String productId, String warehouseId);

    boolean existsByTenantIdAndProductIdAndWarehouseId(String tenantId, String productId, String warehouseId);

    @Query(value = "SELECT sl FROM StockLevelClass sl JOIN FETCH sl.product LEFT JOIN FETCH sl.warehouse WHERE sl.tenantId = :tenantId AND sl.warehouseId = :warehouseId",
           countQuery = "SELECT COUNT(sl) FROM StockLevelClass sl WHERE sl.tenantId = :tenantId AND sl.warehouseId = :warehouseId")
    Page<StockLevelClass> findByTenantIdAndWarehouseIdWithProduct(@Param("tenantId") String tenantId, @Param("warehouseId") String warehouseId, Pageable pageable);

    @Query("SELECT s FROM StockLevelClass s JOIN FETCH s.product WHERE s.tenantId = :tenantId AND s.quantity <= s.reorderPoint")
    List<StockLevelClass> findLowStockByReorderPoint(@Param("tenantId") String tenantId);

    @Query(value = "SELECT s FROM StockLevelClass s JOIN FETCH s.product WHERE s.tenantId = :tenantId AND s.quantity <= s.reorderPoint",
           countQuery = "SELECT COUNT(s) FROM StockLevelClass s WHERE s.tenantId = :tenantId AND s.quantity <= s.reorderPoint")
    Page<StockLevelClass> findLowStockByReorderPoint(@Param("tenantId") String tenantId, Pageable pageable);
}
