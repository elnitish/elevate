package com.elevate.insc.repository;

import com.elevate.insc.entity.WarehouseTransferClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseTransferRepository extends JpaRepository<WarehouseTransferClass, String> {

    @Query(value = "SELECT t FROM WarehouseTransferClass t LEFT JOIN FETCH t.fromWarehouse LEFT JOIN FETCH t.toWarehouse LEFT JOIN FETCH t.product WHERE t.tenantId = :tenantId",
           countQuery = "SELECT COUNT(t) FROM WarehouseTransferClass t WHERE t.tenantId = :tenantId")
    Page<WarehouseTransferClass> findByTenantIdWithDetails(@Param("tenantId") String tenantId, Pageable pageable);

    @Query(value = "SELECT t FROM WarehouseTransferClass t LEFT JOIN FETCH t.fromWarehouse LEFT JOIN FETCH t.toWarehouse LEFT JOIN FETCH t.product WHERE t.tenantId = :tenantId AND t.status = :status",
           countQuery = "SELECT COUNT(t) FROM WarehouseTransferClass t WHERE t.tenantId = :tenantId AND t.status = :status")
    Page<WarehouseTransferClass> findByTenantIdAndStatusWithDetails(@Param("tenantId") String tenantId, @Param("status") WarehouseTransferClass.Status status, Pageable pageable);

    Optional<WarehouseTransferClass> findByTenantIdAndId(String tenantId, String id);
}
