package com.elevate.fna.repository;

import com.elevate.fna.entity.CustomerClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerClassRepo extends JpaRepository<CustomerClass, Long> {

    List<CustomerClass> findAll(Sort sort);
}
