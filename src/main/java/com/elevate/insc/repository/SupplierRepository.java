package com.elevate.insc.repository;

import com.elevate.insc.entity.SupplierClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierClass, String> {
    
    // Find suppliers by tenant
    List<SupplierClass> findByTenantId(String tenantId);
    
    // Find supplier by tenant and ID
    Optional<SupplierClass> findByTenantIdAndId(String tenantId, String id);
    
    // Check if supplier exists by tenant and ID
    boolean existsByTenantIdAndId(String tenantId, String id);
    
    // Check if supplier exists by tenant
    boolean existsByTenantId(String tenantId);
    
    // Find supplier by tenant and name
    Optional<SupplierClass> findByTenantIdAndName(String tenantId, String name);
    
    // Check if supplier name exists in tenant
    boolean existsByTenantIdAndName(String tenantId, String name);
}
