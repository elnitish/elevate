package com.elevate.hrs.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.hrs.entity.LeaveRequestClass;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequestClass, Long> {

    List<LeaveRequestClass> findByTenantId(String tenantId);

    List<LeaveRequestClass> findByTenantIdAndStatus(String tenantId, String status);

    List<LeaveRequestClass> findByTenantIdAndEmployeeId(String tenantId, Long employeeId);

    List<LeaveRequestClass> findByTenantIdAndEmployeeIdAndStatus(String tenantId, Long employeeId, String status);

    List<LeaveRequestClass> findByTenantIdAndStartDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    List<LeaveRequestClass> findByTenantIdOrderByStartDateDesc(String tenantId);

}
