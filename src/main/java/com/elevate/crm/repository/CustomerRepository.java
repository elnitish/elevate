package com.elevate.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.crm.entity.CustomerClass;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerClass, Long> {

    List<CustomerClass> findByTenantId(String tenantId);
    Page<CustomerClass> findByTenantId(String tenantId, Pageable pageable);

    Optional<CustomerClass> findByTenantIdAndId(String tenantId, Long id);

    boolean existsByTenantIdAndPhone(String tenantId, String phone);
}


