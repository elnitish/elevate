package com.elevate.crm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.crm.entity.CustomerLedgerClass;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedgerClass, Long> {

    List<CustomerLedgerClass> findByTenantId(String tenantId);

    List<CustomerLedgerClass> findByTenantIdAndCustomer_Id(String tenantId, Long customerId);

    Page<CustomerLedgerClass> findByTenantId(String tenantId, Pageable pageable);

    Page<CustomerLedgerClass> findByTenantIdAndCustomer_Id(String tenantId, Long customerId, Pageable pageable);
}


