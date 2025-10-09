package com.elevate.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.crm.entity.CustomerLedgerClass;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedgerClass, Long> {

    List<CustomerLedgerClass> findByTenantId(String tenantId);

    List<CustomerLedgerClass> findByTenantIdAndCustomerId(String tenantId, Long customerId);
}


