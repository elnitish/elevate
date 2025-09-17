package com.elevate.hrs.repository;

import com.elevate.hrs.entity.EmployeeAttendenceClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeAttendenceRepo extends JpaRepository<EmployeeAttendenceClass, Long> {
    List<EmployeeAttendenceClass> findAllByEmployeeId(Long id, Sort sort);

}
