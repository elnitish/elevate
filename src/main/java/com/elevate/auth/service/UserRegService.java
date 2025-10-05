package com.elevate.auth.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.dto.UserClassResDTO;
import com.elevate.auth.entity.Role;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.entity.UserRole;
import com.elevate.auth.entity.UserRoleId;
import com.elevate.auth.repository.RoleRepository;
import com.elevate.auth.repository.UserRepo;
import com.elevate.auth.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRegService {

    private final UserRepo userRepo;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserRegService(UserRepo userRepo, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepo = userRepo;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public ApiResponse<?> registerNewUser(UserClassReqDTO userClassReqDTO){
        // Check if username already exists
        if(userRepo.existsByUsername(userClassReqDTO.getUsername())){
            return new ApiResponse<>("Username already exists",409,null);
        }
        
        // Check if email already exists
        if(userRepo.existsByEmail(userClassReqDTO.getEmail())){
            return new ApiResponse<>("Email already exists",409,null);
        }
        
        // Create new user
        UserClass newUser = new UserClass(
                userClassReqDTO.getUsername(),
                userClassReqDTO.getEmail(),
                bCryptPasswordEncoder.encode(userClassReqDTO.getPassword())
        );
        
        UserClass savedUser = userRepo.save(newUser);
        
        // Assign roles if provided
        if (userClassReqDTO.getRoles() != null && !userClassReqDTO.getRoles().isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            for (String roleName : userClassReqDTO.getRoles()) {
                Optional<Role> role = roleRepository.findByName(roleName);
                if (role.isPresent()) {
                    UserRole userRole = new UserRole(savedUser.getId(), role.get().getId());
                    userRoles.add(userRole);
                }
            }
            userRoleRepository.saveAll(userRoles);
        }
        
        // Load user with roles for response
        UserClass userWithRoles = userRepo.findById(savedUser.getId()).orElse(savedUser);
        UserClassResDTO userClassResponse = new UserClassResDTO(userWithRoles);
        return new ApiResponse<>("User registered successfully",200,userClassResponse);
    }

    public ApiResponse<?> returnUser(UserClassReqDTO userClassReqDTO) {
        Optional<UserClass> userOpt = userRepo.findByUsername(userClassReqDTO.getUsername());
        if(userOpt.isEmpty()){
            return new ApiResponse<>("User not found",404,null);
        }
        
        UserClass user = userOpt.get();
        if(!user.getIsActive()){
            return new ApiResponse<>("User account is deactivated",403,null);
        }
        
        if(bCryptPasswordEncoder.matches(userClassReqDTO.getPassword(), user.getPasswordHash())){
            UserClassResDTO userResponse = new UserClassResDTO(user);
            return new ApiResponse<>("User found successfully",200,userResponse);
        }
        else {
            return new ApiResponse<>("Incorrect password",401,null);
        }
    }
    
    public ApiResponse<?> getUserByEmail(String email) {
        Optional<UserClass> userOpt = userRepo.findByEmail(email);
        if(userOpt.isEmpty()){
            return new ApiResponse<>("User not found",404,null);
        }
        
        UserClass user = userOpt.get();
        UserClassResDTO userResponse = new UserClassResDTO(user);
        return new ApiResponse<>("User found successfully",200,userResponse);
    }
    
    @Transactional
    public ApiResponse<?> assignRolesToUser(Long userId, List<String> roleNames) {
        Optional<UserClass> userOpt = userRepo.findById(userId);
        if(userOpt.isEmpty()){
            return new ApiResponse<>("User not found",404,null);
        }
        
        List<UserRole> userRoles = new ArrayList<>();
        
        for (String roleName : roleNames) {
            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isPresent()) {
                if (!userRoleRepository.existsByUserIdAndRoleId(userId, role.get().getId())) {
                    UserRole userRole = new UserRole(userId, role.get().getId());
                    userRoles.add(userRole);
                }
            }
        }
        
        if (!userRoles.isEmpty()) {
            userRoleRepository.saveAll(userRoles);
        }
        
        return new ApiResponse<>("Roles assigned successfully",200,null);
    }
    
    @Transactional
    public ApiResponse<?> removeRolesFromUser(Long userId, List<String> roleNames) {
        Optional<UserClass> userOpt = userRepo.findById(userId);
        if(userOpt.isEmpty()){
            return new ApiResponse<>("User not found",404,null);
        }
        
        for (String roleName : roleNames) {
            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isPresent()) {
                UserRoleId userRoleId = new UserRoleId(userId, role.get().getId());
                userRoleRepository.deleteById(userRoleId);
            }
        }
        
        return new ApiResponse<>("Roles removed successfully",200,null);
    }
}
