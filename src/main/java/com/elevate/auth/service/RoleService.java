package com.elevate.auth.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.RoleDTO;
import com.elevate.auth.entity.Role;
import com.elevate.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public ApiResponse<?> createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            return new ApiResponse<>("Role already exists", 409, null);
        }
        
        Role role = new Role(roleDTO.getName());
        Role savedRole = roleRepository.save(role);
        RoleDTO responseDTO = new RoleDTO(savedRole.getId(), savedRole.getName());
        
        return new ApiResponse<>("Role created successfully", 200, responseDTO);
    }

    public ApiResponse<?> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Roles retrieved successfully", 200, roleDTOs);
    }

    public ApiResponse<?> getRoleById(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            RoleDTO roleDTO = new RoleDTO(role.get().getId(), role.get().getName());
            return new ApiResponse<>("Role found", 200, roleDTO);
        }
        return new ApiResponse<>("Role not found", 404, null);
    }

    public ApiResponse<?> updateRole(Integer id, RoleDTO roleDTO) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            return new ApiResponse<>("Role not found", 404, null);
        }
        
        Role role = roleOpt.get();
        if (!role.getName().equals(roleDTO.getName()) && roleRepository.existsByName(roleDTO.getName())) {
            return new ApiResponse<>("Role name already exists", 409, null);
        }
        
        role.setName(roleDTO.getName());
        Role updatedRole = roleRepository.save(role);
        RoleDTO responseDTO = new RoleDTO(updatedRole.getId(), updatedRole.getName());
        
        return new ApiResponse<>("Role updated successfully", 200, responseDTO);
    }

    public ApiResponse<?> deleteRole(Integer id) {
        if (!roleRepository.existsById(id)) {
            return new ApiResponse<>("Role not found", 404, null);
        }
        
        roleRepository.deleteById(id);
        return new ApiResponse<>("Role deleted successfully", 200, null);
    }
}
