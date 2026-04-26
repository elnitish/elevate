package com.elevate.insc.repository;

import com.elevate.insc.entity.StockMovementClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementClass, String> {

    List<StockMovementClass> findByTenantIdAndProductId(String tenantId, String productId);

    List<StockMovementClass> findByTenantId(String tenantId);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId")
    Page<StockMovementClass> findByTenantIdWithProduct(@Param("tenantId") String tenantId, Pageable pageable);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId AND sm.productId = :productId",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId AND sm.productId = :productId")
    Page<StockMovementClass> findByTenantIdAndProductIdWithProduct(@Param("tenantId") String tenantId, @Param("productId") String productId, Pageable pageable);

    List<StockMovementClass> findByTenantIdAndPurchaseOrderId(String tenantId, String purchaseOrderId);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId AND sm.purchaseOrderId = :purchaseOrderId",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId AND sm.purchaseOrderId = :purchaseOrderId")
    Page<StockMovementClass> findByTenantIdAndPurchaseOrderIdWithProduct(@Param("tenantId") String tenantId, @Param("purchaseOrderId") String purchaseOrderId, Pageable pageable);

    List<StockMovementClass> findByTenantIdAndInvoiceId(String tenantId, String invoiceId);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId AND sm.invoiceId = :invoiceId",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId AND sm.invoiceId = :invoiceId")
    Page<StockMovementClass> findByTenantIdAndInvoiceIdWithProduct(@Param("tenantId") String tenantId, @Param("invoiceId") String invoiceId, Pageable pageable);

    List<StockMovementClass> findByTenantIdAndType(String tenantId, StockMovementClass.Type type);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId AND sm.type = :type",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId AND sm.type = :type")
    Page<StockMovementClass> findByTenantIdAndTypeWithProduct(@Param("tenantId") String tenantId, @Param("type") StockMovementClass.Type type, Pageable pageable);

    List<StockMovementClass> findByTenantIdAndDateBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT sm FROM StockMovementClass sm JOIN FETCH sm.product WHERE sm.tenantId = :tenantId AND sm.date BETWEEN :startDate AND :endDate",
           countQuery = "SELECT COUNT(sm) FROM StockMovementClass sm WHERE sm.tenantId = :tenantId AND sm.date BETWEEN :startDate AND :endDate")
    Page<StockMovementClass> findByTenantIdAndDateBetweenWithProduct(@Param("tenantId") String tenantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COALESCE(SUM(CASE WHEN s.type = 'IN' THEN s.quantity ELSE -s.quantity END), 0) " +
           "FROM StockMovementClass s WHERE s.tenantId = :tenantId AND s.productId = :productId")
    Integer getNetStockMovementByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovementClass s " +
           "WHERE s.tenantId = :tenantId AND s.productId = :productId AND s.type = 'IN'")
    Integer getTotalInMovementsByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovementClass s " +
           "WHERE s.tenantId = :tenantId AND s.productId = :productId AND s.type = 'OUT'")
    Integer getTotalOutMovementsByTenantAndProduct(@Param("tenantId") String tenantId, @Param("productId") String productId);
}
