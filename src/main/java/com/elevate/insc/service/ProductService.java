package com.elevate.insc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.elevate.insc.dto.ProductReqDTO;
import com.elevate.insc.dto.UpdateProductReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private ProductClassRepo productClassRepo;
    private StockLevelService stockLevelService;
    private CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductClassRepo productClassRepo, StockLevelService stockLevelService, CategoryRepository categoryRepository) {
        this.productClassRepo = productClassRepo;
        this.stockLevelService = stockLevelService;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ApiResponse<?> createNewProduct(ProductReqDTO product, String tenantID) {
        // Validate that the category exists
        if (!categoryRepository.existsById(product.getCategoryId())) {
            return new ApiResponse<>("Category with ID " + product.getCategoryId() + " does not exist. Please create a category first.", 400, null);
        }
        
        // Generate a new UUID for the product
        String productId = java.util.UUID.randomUUID().toString();
        System.out.println(tenantID);
        
        // Create the ProductClass entity with all required fields
        ProductClass productClass = new ProductClass(
            productId,
            tenantID,
            product.getCategoryId(),
            product.getName(),
            product.getDescription(),
            product.getCostPrice(),
            product.getSellingPrice()
        );
        
        // Save the product to database
        ProductClass newProduct = productClassRepo.save(productClass);
        
        // Create initial stock level for the product
        stockLevelService.createInitialStockLevel(tenantID, productId);
        
        return new ApiResponse<>("Product created successfully", 200, newProduct);
    }

    public ApiResponse<?> returnAllProducts(String tenantID) {
        List<ProductClass> allProducts = productClassRepo.findByTenantId(tenantID);
        return new ApiResponse<>("All products returned successfully",200,allProducts);
    }

    public ApiResponse<?> returnProductWithID(String tenantID, String productId) {
        Optional<ProductClass> product = productClassRepo.findByTenantIdAndId(tenantID,productId);
        return new ApiResponse<>("All products returned successfully",200,product.get());
    }
    
    public Optional<ProductClass> getProductById(String productId) {
        return productClassRepo.findById(productId);
    }

    public ApiResponse<?> returnAllProductStock() {
        // This method should be updated to use the new StockLevelService
        // For now, return a message indicating it needs to be updated
        return new ApiResponse<>("This method needs to be updated to use StockLevelService",200,null);
    }

    public void addStock(PurchaseOrderClass order) {
        List<PurchaseOrderItemClass> items = order.getItems();
        for(PurchaseOrderItemClass item : items) {
            ProductClass product = item.getProduct();
            // Use the new StockLevelService to increase stock
            stockLevelService.increaseStock(order.getTenantId(), product.getId(), item.getQuantity());
        }
    }

    public void deductStock(List<InvoiceItemReqDTO> items) {
        for (InvoiceItemReqDTO item : items) {
            Optional<ProductClass> product = productClassRepo.findById(item.getProductId());
            if (product.isPresent()) {
                // Use the new StockLevelService to decrease stock
                stockLevelService.decreaseStock(product.get().getTenantId(), item.getProductId(), item.getQuantity());
            }
        }
    }

    public ApiResponse<?> updateProductInDB(String tenantID, UpdateProductReqDTO updateProductReqDTO) {
        Optional<ProductClass> product = productClassRepo.findByTenantIdAndId(tenantID, updateProductReqDTO.getId());
        if(product.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        ProductClass productClass = product.get();
        if(!updateProductReqDTO.getName().isEmpty()) {
            productClass.setName(updateProductReqDTO.getName());
        }
        if(!updateProductReqDTO.getDescription().isEmpty()) {
            productClass.setDescription(updateProductReqDTO.getDescription());
        }
        if(!updateProductReqDTO.getCostPrice().equals(BigDecimal.ZERO)) {
            productClass.setCostPrice(updateProductReqDTO.getCostPrice());
        }
        if(!updateProductReqDTO.getSellingPrice().equals(BigDecimal.ZERO)) {
            productClass.setSellingPrice(updateProductReqDTO.getSellingPrice());
        }
        if(!updateProductReqDTO.getCategoryId().isEmpty()) {
            productClass.setCategoryId(updateProductReqDTO.getCategoryId());
        }
        ProductClass updatedProduct = productClassRepo.save(productClass);
        return new ApiResponse<>("Product updated successfully", 201, updatedProduct);

    }

    public ApiResponse<?> deleteProductFromDB(String tenantID, String productId) {
        Optional<ProductClass> product = productClassRepo.findByTenantIdAndId(tenantID, productId);
        if(product.isEmpty()) {
            return new ApiResponse<>("Product not found", 404, null);
        }
        ProductClass productClass = product.get();
        productClassRepo.delete(productClass);
        return new ApiResponse<>("Product deleted successfully", 200, null);
    }
}
