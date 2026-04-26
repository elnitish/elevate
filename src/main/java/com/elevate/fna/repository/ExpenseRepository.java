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

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseClass e WHERE e.tenantId = :tenantId AND e.expenseDate BETWEEN :start AND :end")
    java.math.BigDecimal sumAmountByTenantAndDateBetween(@org.springframework.data.repository.query.Param("tenantId") String tenantId, @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, @org.springframework.data.repository.query.Param("end") java.time.LocalDate end);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(e) FROM ExpenseClass e WHERE e.tenantId = :tenantId AND e.expenseDate BETWEEN :start AND :end")
    long countByTenantAndDateBetween(@org.springframework.data.repository.query.Param("tenantId") String tenantId, @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, @org.springframework.data.repository.query.Param("end") java.time.LocalDate end);
}
