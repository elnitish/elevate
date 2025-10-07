package com.elevate.insc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.insc.entity.CategoryClass;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryClass, String> {
    
    List<CategoryClass> findByTenantId(String tenantId);
    
    Optional<CategoryClass> findByTenantIdAndName(String tenantId, String name);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
    
    boolean existsByTenantId(String tenantId);
}
