package com.elevate.fna.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.fna.entity.ExpenseClass;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseClass, Long> {

    List<ExpenseClass> findByTenantId(String tenantId);

    List<ExpenseClass> findByTenantIdAndStatus(String tenantId, String status);

    List<ExpenseClass> findByTenantIdAndCategory(String tenantId, String category);

    List<ExpenseClass> findByTenantIdAndExpenseDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    List<ExpenseClass> findByTenantIdOrderByExpenseDateDesc(String tenantId);

}
