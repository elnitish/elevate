package com.elevate.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.auth.entity.UserClass;

@Repository
public interface UserRepo extends JpaRepository<UserClass,Long> {
    Optional<UserClass> findByUsername(String username);
    Optional<UserClass> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
