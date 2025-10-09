package com.elevate.fna.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.elevate.crm.entity.CustomerClass;

@Repository
public interface CustomerClassRepo extends JpaRepository<CustomerClass, Long> {

    @Override
    List<CustomerClass> findAll(Sort sort);
}
