package com.elevate.auth.repository;

import com.elevate.auth.entity.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserClass, String> {
    Optional<UserClass> findByTenantIdAndUsername(String tenantId, String username);
    List<UserClass> findByTenantId(String tenantId);
    boolean existsByTenantIdAndUsername(String tenantId, String username);
    boolean existsByTenantIdAndEmail(String tenantId, String email);
    
    @Query("SELECT u FROM UserClass u WHERE u.tenantId = :tenantId AND u.email = :email")
    Optional<UserClass> findByTenantIdAndEmail(@Param("tenantId") String tenantId, @Param("email") String email);
}