package com.elevate.insc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.insc.entity.ProductClass;

@Repository
public interface ProductClassRepo extends JpaRepository<ProductClass, String> {
    
    List<ProductClass> findByTenantId(String tenantId);
    
    List<ProductClass> findByTenantIdAndCategoryId(String tenantId, String categoryId);
    
    Optional<ProductClass> findByTenantIdAndCategoryIdAndName(String tenantId, String categoryId, String name);
    
    boolean existsByTenantIdAndCategoryIdAndName(String tenantId, String categoryId, String name);
    
    boolean existsByTenantId(String tenantId);
    
    boolean existsByCategoryId(String categoryId);

    Optional<ProductClass> findByTenantIdAndId(String tenantId, String id);
}