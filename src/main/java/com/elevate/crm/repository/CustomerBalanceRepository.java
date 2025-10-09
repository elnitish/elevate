package com.elevate.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.crm.entity.CustomerBalanceClass;
import com.elevate.crm.entity.CustomerBalanceId;

@Repository
public interface CustomerBalanceRepository extends JpaRepository<CustomerBalanceClass, CustomerBalanceId> {

    List<CustomerBalanceClass> findByIdTenantId(String tenantId);

    Optional<CustomerBalanceClass> findByIdTenantIdAndIdCustomerId(String tenantId, Long customerId);
}


