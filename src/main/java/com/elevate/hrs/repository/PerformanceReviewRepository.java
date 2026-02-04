package com.elevate.hrs.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.hrs.entity.PerformanceReviewClass;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReviewClass, Long> {

    List<PerformanceReviewClass> findByTenantId(String tenantId);

    List<PerformanceReviewClass> findByTenantIdAndEmployeeId(String tenantId, Long employeeId);

    List<PerformanceReviewClass> findByTenantIdAndReviewDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    List<PerformanceReviewClass> findByTenantIdOrderByReviewDateDesc(String tenantId);

}
