package com.elevate.pricing.repository;

import com.elevate.crm.entity.CustomerClass;
import com.elevate.pricing.entity.PriceListClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceListClass, String> {

    List<PriceListClass> findByTenantId(String tenantId);

    List<PriceListClass> findByTenantIdAndIsActiveTrue(String tenantId);

    Optional<PriceListClass> findByTenantIdAndId(String tenantId, String id);

    List<PriceListClass> findByTenantIdAndCustomerTypeAndIsActiveTrue(String tenantId, CustomerClass.CustomerType customerType);

    Optional<PriceListClass> findByTenantIdAndIsDefaultTrueAndIsActiveTrue(String tenantId);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
