package com.elevate.insc.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.insc.dto.ProductReqDTO;
import com.elevate.insc.dto.ProductResDTO;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.repository.CategoryRepository;

@Service
public class ProductService {
    
    private final ProductClassRepo productClassRepo;
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final StockLevelService stockLevelService;
    
    @Autowired
    public ProductService(ProductClassRepo productClassRepo, 
                         CategoryRepository categoryRepository,
                         TenantRepository tenantRepository,
                         StockLevelService stockLevelService) {
        this.productClassRepo = productClassRepo;
        this.categoryRepository = categoryRepository;
        this.tenantRepository = tenantRepository;
        this.stockLevelService = stockLevelService;
    }
    
    @Transactional
    public ApiResponse<?> createProduct(ProductReqDTO productReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(productReqDTO.getTenantId())) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Validate category exists and belongs to tenant
        if (!categoryRepository.existsById(productReqDTO.getCategoryId())) {
            return new ApiResponse<>("Category not found", 404, null);
        }
        
        // Check if product name already exists in this tenant and category
        if (productClassRepo.existsByTenantIdAndCategoryIdAndName(
            productReqDTO.getTenantId(), 
            productReqDTO.getCategoryId(), 
            productReqDTO.getName())) {
            return new ApiResponse<>("Product name already exists in this category", 409, null);
        }
        
        // Generate UUID for product
        String productId = UUID.randomUUID().toString();
        
        // Create product entity
        ProductClass newProduct = new ProductClass(
            productId,
            productReqDTO.getTenantId(),
            productReqDTO.getCategoryId(),
            productReqDTO.getName().trim(),
            productReqDTO.getDescription(),
            productReqDTO.getCostPrice(),
            productReqDTO.getSellingPrice()
        );
        
        ProductClass savedProduct = productClassRepo.save(newProduct);
        
        // Create initial stock level (quantity = 0) for the new product
        stockLevelService.createInitialStockLevel(productReqDTO.getTenantId(), productId);
        
        ProductResDTO responseDTO = new ProductResDTO(savedProduct);
        
        return new ApiResponse<>("Product created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getProductsByTenant(String tenantId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<ProductClass> products = productClassRepo.findByTenantId(tenantId);
        List<ProductResDTO> productDTOs = products.stream()
                .map(ProductResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Products retrieved successfully", 200, productDTOs);
    }
    
    public ApiResponse<?> getProductsByCategory(String tenantId, String categoryId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<ProductClass> products = productClassRepo.findByTenantIdAndCategoryId(tenantId, categoryId);
        List<ProductResDTO> productDTOs = products.stream()
                .map(ProductResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Products retrieved successfully", 200, productDTOs);
    }
    
    public ApiResponse<?> getProductById(String tenantId, String productId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<ProductClass> productOpt = productClassRepo.findById(productId);
        if (productOpt.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        
        ProductClass product = productOpt.get();
        
        // Verify product belongs to tenant
        if (!product.getTenantId().equals(tenantId)) {
            return new ApiResponse<>("Product does not belong to this tenant", 403, null);
        }
        
        ProductResDTO responseDTO = new ProductResDTO(product);
        return new ApiResponse<>("Product retrieved successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> updateProduct(String tenantId, String productId, ProductReqDTO productReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<ProductClass> productOpt = productClassRepo.findById(productId);
        if (productOpt.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        
        ProductClass product = productOpt.get();
        
        // Verify product belongs to tenant
        if (!product.getTenantId().equals(tenantId)) {
            return new ApiResponse<>("Product does not belong to this tenant", 403, null);
        }
        
        // Validate category exists
        if (!categoryRepository.existsById(productReqDTO.getCategoryId())) {
            return new ApiResponse<>("Category not found", 404, null);
        }
        
        // Check if new name already exists in this tenant and category (excluding current product)
        if (!product.getName().equals(productReqDTO.getName()) && 
            productClassRepo.existsByTenantIdAndCategoryIdAndName(
                productReqDTO.getTenantId(), 
                productReqDTO.getCategoryId(), 
                productReqDTO.getName())) {
            return new ApiResponse<>("Product name already exists in this category", 409, null);
        }
        
        // Update product fields
        product.setCategoryId(productReqDTO.getCategoryId());
        product.setName(productReqDTO.getName().trim());
        product.setDescription(productReqDTO.getDescription());
        product.setCostPrice(productReqDTO.getCostPrice());
        product.setSellingPrice(productReqDTO.getSellingPrice());
        
        ProductClass updatedProduct = productClassRepo.save(product);
        ProductResDTO responseDTO = new ProductResDTO(updatedProduct);
        
        return new ApiResponse<>("Product updated successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> deleteProduct(String tenantId, String productId) {
        // Validate tenant exists
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<ProductClass> productOpt = productClassRepo.findById(productId);
        if (productOpt.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        
        ProductClass product = productOpt.get();
        
        // Verify product belongs to tenant
        if (!product.getTenantId().equals(tenantId)) {
            return new ApiResponse<>("Product does not belong to this tenant", 403, null);
        }
        
        // Check if product has stock (optional business rule)
        Integer currentStock = stockLevelService.getCurrentStock(tenantId, productId);
        if (currentStock > 0) {
            return new ApiResponse<>("Cannot delete product with existing stock. Current stock: " + currentStock, 400, null);
        }
        
        productClassRepo.deleteById(productId);
        
        return new ApiResponse<>("Product deleted successfully", 200, null);
    }
}
