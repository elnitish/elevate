package com.elevate.pricing.repository;

import com.elevate.pricing.entity.DiscountClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountClass, String> {

    List<DiscountClass> findByTenantId(String tenantId);

    List<DiscountClass> findByTenantIdAndIsActiveTrue(String tenantId);

    Optional<DiscountClass> findByTenantIdAndId(String tenantId, String id);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
