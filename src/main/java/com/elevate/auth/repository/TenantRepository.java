package com.elevate.auth.repository;

import com.elevate.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    Optional<Tenant> findByName(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
