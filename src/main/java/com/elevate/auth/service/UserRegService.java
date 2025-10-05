package com.elevate.auth.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.dto.UserClassResDTO;
import com.elevate.auth.entity.AuthCredentials;
import com.elevate.auth.entity.Tenant;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.repository.AuthCredentialsRepository;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserRegService {

    private final UserRepo userRepo;
    private final TenantRepository tenantRepository;
    private final AuthCredentialsRepository authCredentialsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserRegService(UserRepo userRepo, TenantRepository tenantRepository, AuthCredentialsRepository authCredentialsRepository) {
        this.userRepo = userRepo;
        this.tenantRepository = tenantRepository;
        this.authCredentialsRepository = authCredentialsRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }


    public ApiResponse<?> returnUser(UserClassReqDTO userClassReqDTO) {
        // Check auth credentials directly
        Optional<AuthCredentials> authOpt = authCredentialsRepository.findByTenantIdAndUsername(
                userClassReqDTO.getTenantId(),
                userClassReqDTO.getUsername()
        );
        
        if(authOpt.isEmpty()){
            return new ApiResponse<>("User credentials not found",404,null);
        }
        
        AuthCredentials authCredentials = authOpt.get();
        
        if(bCryptPasswordEncoder.matches(userClassReqDTO.getPassword(), authCredentials.getPasswordHash())){
            // Only fetch user data after successful authentication
            Optional<UserClass> userOpt = userRepo.findByTenantIdAndUsername(
                    userClassReqDTO.getTenantId(), 
                    userClassReqDTO.getUsername()
            );
            
            if(userOpt.isPresent()){
                UserClassResDTO userResponse = new UserClassResDTO(userOpt.get());
                return new ApiResponse<>("User found successfully",200,userResponse);
            } else {
                return new ApiResponse<>("User data not found",404,null);
            }
        }
        else {
            return new ApiResponse<>("Incorrect password",401,null);
        }
    }
    
    
    public ApiResponse<?> getUsersByTenant(String tenantId) {
        // Check if tenant exists
        if(!tenantRepository.existsById(tenantId)){
            return new ApiResponse<>("Tenant not found",404,null);
        }
        
        return new ApiResponse<>("Users retrieved successfully",200,userRepo.findByTenantId(tenantId));
    }
}
