package com.elevate.insc.repository;

import com.elevate.insc.entity.WarehouseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseClass, String> {

    List<WarehouseClass> findByTenantId(String tenantId);

    List<WarehouseClass> findByTenantIdAndIsActiveTrue(String tenantId);

    Optional<WarehouseClass> findByTenantIdAndId(String tenantId, String id);

    Optional<WarehouseClass> findByTenantIdAndCode(String tenantId, String code);

    Optional<WarehouseClass> findByTenantIdAndIsDefaultTrue(String tenantId);

    boolean existsByTenantIdAndCode(String tenantId, String code);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
