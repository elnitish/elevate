package com.elevate.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elevate.auth.entity.UserRole;
import com.elevate.auth.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.userId = :userId")
    List<UserRole> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.roleId = :roleId")
    List<UserRole> findByRoleId(@Param("roleId") Integer roleId);
    
    void deleteByUserId(Long userId);
    
    boolean existsByUserIdAndRoleId(Long userId, Integer roleId);
    
    void deleteByUserIdAndRoleId(Long userId, Integer roleId);
}
