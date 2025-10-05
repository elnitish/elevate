package com.elevate.auth.repository;

import com.elevate.auth.entity.AuthCredentials;
import com.elevate.auth.entity.AuthCredentialsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, AuthCredentialsId> {
    Optional<AuthCredentials> findByTenantIdAndUsername(String tenantId, String username);
    boolean existsByTenantIdAndUsername(String tenantId, String username);
}
