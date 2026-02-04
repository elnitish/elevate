package com.elevate.fna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.fna.entity.PayrollClass;

@Repository
public interface PayrollRepository extends JpaRepository<PayrollClass, Long> {

    List<PayrollClass> findByTenantId(String tenantId);

    List<PayrollClass> findByTenantIdAndStatus(String tenantId, String status);

    List<PayrollClass> findByTenantIdAndEmployeeId(String tenantId, Long employeeId);

    Optional<PayrollClass> findByTenantIdAndEmployeeIdAndYearMonth(String tenantId, Long employeeId, String yearMonth);

    List<PayrollClass> findByTenantIdAndYearMonth(String tenantId, String yearMonth);

    List<PayrollClass> findByTenantIdOrderByYearMonthDesc(String tenantId);

}
