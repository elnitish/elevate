package com.elevate.auth.repository;

import com.elevate.auth.entity.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, String> {
    
    Optional<SessionToken> findBySessionToken(String sessionToken);
    
    @Query("SELECT st FROM SessionToken st WHERE st.tenantId = :tenantId AND st.username = :username")
    Optional<SessionToken> findByTenantIdAndUsername(@Param("tenantId") String tenantId, @Param("username") String username);
    
    void deleteBySessionToken(String sessionToken);
    
    void deleteByTenantIdAndUsername(String tenantId, String username);
    
    boolean existsBySessionToken(String sessionToken);
}
