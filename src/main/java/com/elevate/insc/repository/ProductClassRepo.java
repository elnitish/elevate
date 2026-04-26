package com.elevate.insc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.insc.entity.ProductClass;

@Repository
public interface ProductClassRepo extends JpaRepository<ProductClass, String> {

    List<ProductClass> findByTenantId(String tenantId);
    Page<ProductClass> findByTenantId(String tenantId, Pageable pageable);

    List<ProductClass> findByTenantIdAndCategoryId(String tenantId, String categoryId);
    Page<ProductClass> findByTenantIdAndCategoryId(String tenantId, String categoryId, Pageable pageable);
    
    Optional<ProductClass> findByTenantIdAndCategoryIdAndName(String tenantId, String categoryId, String name);
    
    boolean existsByTenantIdAndCategoryIdAndName(String tenantId, String categoryId, String name);
    
    boolean existsByTenantId(String tenantId);
    
    boolean existsByCategoryId(String categoryId);

    Optional<ProductClass> findByTenantIdAndId(String tenantId, String id);

    Optional<ProductClass> findByTenantIdAndSku(String tenantId, String sku);

    Optional<ProductClass> findByTenantIdAndBarcode(String tenantId, String barcode);

    boolean existsByTenantIdAndSku(String tenantId, String sku);

    boolean existsByTenantIdAndBarcode(String tenantId, String barcode);
}