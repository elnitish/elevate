package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.WarehouseReqDTO;
import com.elevate.insc.dto.WarehouseResDTO;
import com.elevate.insc.entity.WarehouseClass;
import com.elevate.insc.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    @CacheEvict(value = "warehouses", key = "#tenantId")
    public ApiResponse<?> createWarehouse(String tenantId, WarehouseReqDTO dto) {
        if (warehouseRepository.existsByTenantIdAndCode(tenantId, dto.getCode())) {
            return new ApiResponse<>("Warehouse with code '" + dto.getCode() + "' already exists", 400, null);
        }
        if (warehouseRepository.existsByTenantIdAndName(tenantId, dto.getName())) {
            return new ApiResponse<>("Warehouse with name '" + dto.getName() + "' already exists", 400, null);
        }

        WarehouseClass warehouse = new WarehouseClass();
        warehouse.setId(java.util.UUID.randomUUID().toString());
        warehouse.setTenantId(tenantId);
        warehouse.setName(dto.getName());
        warehouse.setCode(dto.getCode().toUpperCase());
        warehouse.setAddress(dto.getAddress());
        warehouse.setIsDefault(false);
        warehouse.setIsActive(true);

        // If requested as default, unset current default
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            unsetCurrentDefault(tenantId);
            warehouse.setIsDefault(true);
        }

        WarehouseClass saved = warehouseRepository.save(warehouse);
        return new ApiResponse<>("Warehouse created successfully", 201, new WarehouseResDTO(saved));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "warehouses", key = "#tenantId")
    public ApiResponse<?> getAllWarehouses(String tenantId) {
        List<WarehouseResDTO> warehouses = warehouseRepository.findByTenantId(tenantId)
                .stream().map(WarehouseResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Warehouses retrieved successfully", 200, warehouses);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getWarehouseById(String tenantId, String warehouseId) {
        Optional<WarehouseClass> warehouse = warehouseRepository.findByTenantIdAndId(tenantId, warehouseId);
        if (warehouse.isEmpty()) {
            return new ApiResponse<>("Warehouse not found", 404, null);
        }
        return new ApiResponse<>("Warehouse retrieved successfully", 200, new WarehouseResDTO(warehouse.get()));
    }

    @Transactional
    @CacheEvict(value = "warehouses", key = "#tenantId")
    public ApiResponse<?> updateWarehouse(String tenantId, String warehouseId, WarehouseReqDTO dto) {
        Optional<WarehouseClass> warehouseOpt = warehouseRepository.findByTenantIdAndId(tenantId, warehouseId);
        if (warehouseOpt.isEmpty()) {
            return new ApiResponse<>("Warehouse not found", 404, null);
        }

        WarehouseClass warehouse = warehouseOpt.get();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            warehouse.setName(dto.getName());
        }
        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            warehouse.setCode(dto.getCode().toUpperCase());
        }
        if (dto.getAddress() != null) {
            warehouse.setAddress(dto.getAddress());
        }
        if (Boolean.TRUE.equals(dto.getIsDefault()) && !warehouse.getIsDefault()) {
            unsetCurrentDefault(tenantId);
            warehouse.setIsDefault(true);
        }

        WarehouseClass saved = warehouseRepository.save(warehouse);
        return new ApiResponse<>("Warehouse updated successfully", 200, new WarehouseResDTO(saved));
    }

    @Transactional
    public ApiResponse<?> deactivateWarehouse(String tenantId, String warehouseId) {
        Optional<WarehouseClass> warehouseOpt = warehouseRepository.findByTenantIdAndId(tenantId, warehouseId);
        if (warehouseOpt.isEmpty()) {
            return new ApiResponse<>("Warehouse not found", 404, null);
        }
        WarehouseClass warehouse = warehouseOpt.get();
        if (warehouse.getIsDefault()) {
            return new ApiResponse<>("Cannot deactivate the default warehouse", 400, null);
        }
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
        return new ApiResponse<>("Warehouse deactivated successfully", 200, null);
    }

    @Transactional(readOnly = true)
    public Optional<WarehouseClass> getDefaultWarehouse(String tenantId) {
        return warehouseRepository.findByTenantIdAndIsDefaultTrue(tenantId);
    }

    private void unsetCurrentDefault(String tenantId) {
        warehouseRepository.findByTenantIdAndIsDefaultTrue(tenantId)
                .ifPresent(w -> {
                    w.setIsDefault(false);
                    warehouseRepository.save(w);
                });
    }
}
