package com.elevate.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.TenantDTO;
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
            TenantDTO tenantDTO = new TenantDTO(tenant.get());
            return new ApiResponse<>("Tenant found", 200, tenantDTO);
        }
        return new ApiResponse<>("Tenant not found", 404, null);
    }

    public ApiResponse<?> updateTenant(String id, TenantDTO tenantDTO) {
        Optional<TenantClass> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isEmpty()) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        TenantClass tenant = tenantOpt.get();
        
        // Check if name is being changed and if new name already exists
        if (!tenant.getName().equals(tenantDTO.getName()) && 
            tenantRepository.existsByName(tenantDTO.getName())) {
            return new ApiResponse<>("Tenant name already exists", 409, null);
        }
        
        // Check if email is being changed and if new email already exists
        if (tenantDTO.getEmail() != null && !tenantDTO.getEmail().isEmpty() &&
            !tenant.getEmail().equals(tenantDTO.getEmail()) && 
            tenantRepository.existsByEmail(tenantDTO.getEmail())) {
            return new ApiResponse<>("Email already exists", 409, null);
        }
        
        tenant.setName(tenantDTO.getName());
        tenant.setEmail(tenantDTO.getEmail());
        
        TenantClass updatedTenant = tenantRepository.save(tenant);
        TenantDTO responseDTO = new TenantDTO(updatedTenant);
        
        return new ApiResponse<>("Tenant updated successfully", 200, responseDTO);
    }

    public ApiResponse<?> createTenant(TenantReqDTO tenantReqDTO) {
        // Generate UUID for tenant
        String tenantId = java.util.UUID.randomUUID().toString();
        
        TenantClass newTenant = new TenantClass(
            tenantId,
            tenantReqDTO.getName(),
            tenantReqDTO.getEmail(),
            TenantClass.PlanType.valueOf(tenantReqDTO.getPlanType().toUpperCase())
        );
        
        TenantClass savedTenant = tenantRepository.save(newTenant);
        TenantDTO responseDTO = new TenantDTO(savedTenant);
        return new ApiResponse<>("Tenant created successfully", 200, responseDTO);
    }
}
