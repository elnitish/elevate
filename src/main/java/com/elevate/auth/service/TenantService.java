package com.elevate.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.TenantResDTO;
import com.elevate.auth.dto.TenantReqDTO;
import com.elevate.auth.entity.TenantClass;
import com.elevate.auth.repository.TenantRepository;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }



    public ApiResponse<?> getTenantById(String id) {
        Optional<TenantClass> tenant = tenantRepository.findById(id);
        if (tenant.isPresent()) {
            TenantResDTO tenantResDTO = new TenantResDTO(tenant.get());
            return new ApiResponse<>("Tenant found", 200, tenantResDTO);
        }
        return new ApiResponse<>("Tenant not found", 404, null);
    }

    public ApiResponse<?> updateTenant(String id, TenantResDTO tenantResDTO) {
        Optional<TenantClass> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isEmpty()) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        TenantClass tenant = tenantOpt.get();
        
        // Check if name is being changed and if new name already exists
        if (!tenant.getName().equals(tenantResDTO.getName()) &&
            tenantRepository.existsByName(tenantResDTO.getName())) {
            return new ApiResponse<>("Tenant name already exists", 409, null);
        }
        
        // Check if email is being changed and if new email already exists
        if (tenantResDTO.getEmail() != null && !tenantResDTO.getEmail().isEmpty() &&
            !tenant.getEmail().equals(tenantResDTO.getEmail()) &&
            tenantRepository.existsByEmail(tenantResDTO.getEmail())) {
            return new ApiResponse<>("Email already exists", 409, null);
        }
        
        tenant.setName(tenantResDTO.getName());
        tenant.setEmail(tenantResDTO.getEmail());
        
        TenantClass updatedTenant = tenantRepository.save(tenant);
        TenantResDTO responseDTO = new TenantResDTO(updatedTenant);
        
        return new ApiResponse<>("Tenant updated successfully", 200, responseDTO);
    }

    public ApiResponse<?> createTenant(TenantReqDTO tenantReqDTO) {
        // Check for duplicate tenant name
        if (tenantRepository.existsByName(tenantReqDTO.getName())) {
            return new ApiResponse<>("Tenant name already exists", 409, null);
        }
        
        // Check for duplicate email
        if (tenantReqDTO.getEmail() != null && !tenantReqDTO.getEmail().isEmpty() 
            && tenantRepository.existsByEmail(tenantReqDTO.getEmail())) {
            return new ApiResponse<>("Email already exists", 409, null);
        }
        
        // Generate UUID for tenant
        String tenantId = java.util.UUID.randomUUID().toString();
        
        TenantClass newTenant = new TenantClass(
            tenantId,
            tenantReqDTO.getName(),
            tenantReqDTO.getEmail(),
            TenantClass.PlanType.valueOf(tenantReqDTO.getPlanType().toUpperCase())
        );
        
        TenantClass savedTenant = tenantRepository.save(newTenant);
        TenantResDTO responseDTO = new TenantResDTO(savedTenant);
        return new ApiResponse<>("Tenant created successfully", 201, responseDTO);
    }
}
