package com.elevate.auth.repository;

import com.elevate.auth.entity.TenantClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<TenantClass, String> {
    Optional<TenantClass> findByName(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
