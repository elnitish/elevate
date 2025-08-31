package com.elevate.auth.repository;

import com.elevate.auth.entity.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserClass,Long> {
    UserClass findByUsername(String username);

}
