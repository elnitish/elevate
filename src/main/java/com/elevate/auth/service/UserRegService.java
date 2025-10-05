package com.elevate.auth.service;

import com.elevate.auth.configuration.TokenUUID;
import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.dto.UserClassResDTO;
import com.elevate.auth.entity.AuthCredentials;
import com.elevate.auth.entity.SessionToken;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.repository.AuthCredentialsRepository;
import com.elevate.auth.repository.SessionTokenRepository;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserRegService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final AuthCredentialsRepository authCredentialsRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenUUID tokenUUID;

    @Autowired
    public UserRegService(UserRepository userRepository, TenantRepository tenantRepository, AuthCredentialsRepository authCredentialsRepository, SessionTokenRepository sessionTokenRepository, TokenUUID tokenUUID) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.authCredentialsRepository = authCredentialsRepository;
        this.sessionTokenRepository = sessionTokenRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.tokenUUID = tokenUUID;
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
            Optional<UserClass> userOpt = userRepository.findByTenantIdAndUsername(
                    userClassReqDTO.getTenantId(), 
                    userClassReqDTO.getUsername()
            );
            
            if(userOpt.isPresent()){
                UserClass user = userOpt.get();
                UserClassResDTO userResponse = new UserClassResDTO(user);
                
                // Generate session token
                String sessionToken = tokenUUID.generateUUID();
                
                // Create and save session token to database
                SessionToken session = new SessionToken(
                    sessionToken,
                    userClassReqDTO.getTenantId(),
                    userClassReqDTO.getUsername(),
                    user.getRole().name()
                );
                sessionTokenRepository.save(session);
                
                // Prepare response with token and user data
                Map<String, Object> response = new HashMap<>();
                response.put("user", userResponse);
                response.put("sessionToken", sessionToken);
                
                return new ApiResponse<>("User found successfully",200,response);
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
        
        return new ApiResponse<>("Users retrieved successfully",200, userRepository.findByTenantId(tenantId));
    }
    
    public ApiResponse<?> validateSessionToken(String sessionToken) {
        Optional<SessionToken> tokenOpt = sessionTokenRepository.findBySessionToken(sessionToken);
        
        if(tokenOpt.isEmpty()){
            return new ApiResponse<>("Invalid session token",401,null);
        }
        
        SessionToken token = tokenOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("tenantId", token.getTenantId());
        response.put("username", token.getUsername());
        response.put("role", token.getRole());
        response.put("createdAt", token.getCreatedAt());
        
        return new ApiResponse<>("Session token valid",200,response);
    }
    
    @Transactional
    public ApiResponse<?> logoutUser(String sessionToken) {
        if(!sessionTokenRepository.existsBySessionToken(sessionToken)){
            return new ApiResponse<>("Session token not found",404,null);
        }
        
        sessionTokenRepository.deleteBySessionToken(sessionToken);
        return new ApiResponse<>("User logged out successfully",200,null);
    }
}
