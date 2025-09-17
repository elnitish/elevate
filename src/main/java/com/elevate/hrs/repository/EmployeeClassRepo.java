package com.elevate.hrs.repository;

import com.elevate.hrs.entity.EmployeeClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeClassRepo extends JpaRepository<EmployeeClass,Long> {

}
