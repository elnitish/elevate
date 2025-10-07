package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.insc.dto.CategoryReqDTO;
import com.elevate.insc.dto.CategoryResDTO;
import com.elevate.insc.entity.CategoryClass;
import com.elevate.insc.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository, TenantRepository tenantRepository) {
        this.categoryRepository = categoryRepository;
        this.tenantRepository = tenantRepository;
    }
    
    @Transactional
    public ApiResponse<?> createCategory(CategoryReqDTO categoryReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(categoryReqDTO.getTenantId())) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Check if category name already exists in this tenant
        if (categoryRepository.existsByTenantIdAndName(categoryReqDTO.getTenantId(), categoryReqDTO.getName())) {
            return new ApiResponse<>("Category name already exists in this tenant", 409, null);
        }
        
        // Generate UUID for category
        String categoryId = UUID.randomUUID().toString();
        
        // Create category entity
        CategoryClass newCategory = new CategoryClass(
            categoryId,
            categoryReqDTO.getTenantId(),
            categoryReqDTO.getName().trim()
        );
        
        CategoryClass savedCategory = categoryRepository.save(newCategory);
        CategoryResDTO responseDTO = new CategoryResDTO(savedCategory);
        
        return new ApiResponse<>("Category created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getCategoriesByTenant(String tenantId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<CategoryClass> categories = categoryRepository.findByTenantId(tenantId);
        List<CategoryResDTO> categoryDTOs = categories.stream()
                .map(CategoryResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Categories retrieved successfully", 200, categoryDTOs);
    }
    
    @Transactional
    public ApiResponse<?> updateCategory(String categoryId, CategoryReqDTO categoryReqDTO) {
        Optional<CategoryClass> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return new ApiResponse<>("Category not found", 404, null);
        }
        
        CategoryClass category = categoryOpt.get();
        
        // Check if new name already exists in this tenant (excluding current category)
        if (!category.getName().equals(categoryReqDTO.getName()) && 
            categoryRepository.existsByTenantIdAndName(categoryReqDTO.getTenantId(), categoryReqDTO.getName())) {
            return new ApiResponse<>("Category name already exists in this tenant", 409, null);
        }
        
        category.setName(categoryReqDTO.getName().trim());
        CategoryClass updatedCategory = categoryRepository.save(category);
        CategoryResDTO responseDTO = new CategoryResDTO(updatedCategory);
        
        return new ApiResponse<>("Category updated successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> deleteCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            return new ApiResponse<>("Category not found", 404, null);
        }
        
        // Check if category has products (this would be handled by foreign key constraint)
        categoryRepository.deleteById(categoryId);
        
        return new ApiResponse<>("Category deleted successfully", 200, null);
    }
    
    public ApiResponse<?> getCategoryById(String categoryId) {
        Optional<CategoryClass> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return new ApiResponse<>("Category not found", 404, null);
        }
        
        CategoryResDTO responseDTO = new CategoryResDTO(categoryOpt.get());
        return new ApiResponse<>("Category retrieved successfully", 200, responseDTO);
    }
}
