package com.elevate.insc.service;

import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.repository.StockLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StockLevelService {
    
    private final StockLevelRepository stockLevelRepository;
    
    @Autowired
    public StockLevelService(StockLevelRepository stockLevelRepository) {
        this.stockLevelRepository = stockLevelRepository;
    }
    
    /**
     * Create initial stock level for a new product (quantity = 0)
     */
    @Transactional
    public void createInitialStockLevel(String tenantId, String productId) {
        // Check if stock level already exists
        if (stockLevelRepository.existsByTenantIdAndProductId(tenantId, productId)) {
            return; // Stock level already exists
        }
        
        String stockLevelId = java.util.UUID.randomUUID().toString();
        StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, 0);
        stockLevelRepository.save(stockLevel);
    }
    
    /**
     * Increase stock quantity for a product
     */
    @Transactional
    public void increaseStock(String tenantId, String productId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
        
        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            stockLevel.setQuantity(stockLevel.getQuantity() + quantity);
            stockLevelRepository.save(stockLevel);
        } else {
            // Create new stock level if it doesn't exist
            String stockLevelId = java.util.UUID.randomUUID().toString();
            StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, quantity);
            stockLevelRepository.save(stockLevel);
        }
    }
    
    /**
     * Decrease stock quantity for a product
     */
    @Transactional
    public boolean decreaseStock(String tenantId, String productId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
        
        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            int currentQuantity = stockLevel.getQuantity();
            
            if (currentQuantity >= quantity) {
                stockLevel.setQuantity(currentQuantity - quantity);
                stockLevelRepository.save(stockLevel);
                return true; // Successfully decreased stock
            } else {
                return false; // Insufficient stock
            }
        } else {
            return false; // No stock level found
        }
    }
    
    /**
     * Get current stock quantity for a product
     */
    public Integer getCurrentStock(String tenantId, String productId) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
        return stockLevelOpt.map(StockLevelClass::getQuantity).orElse(0);
    }
    
    /**
     * Check if sufficient stock is available
     */
    public boolean hasSufficientStock(String tenantId, String productId, Integer requiredQuantity) {
        Integer currentStock = getCurrentStock(tenantId, productId);
        return currentStock >= requiredQuantity;
    }
    
    /**
     * Get all stock levels for a tenant
     */
    public List<StockLevelClass> getStockLevelsByTenant(String tenantId) {
        return stockLevelRepository.findByTenantId(tenantId);
    }
    
    /**
     * Get stock level for a specific product
     */
    public Optional<StockLevelClass> getStockLevel(String tenantId, String productId) {
        return stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
    }
    
    /**
     * Get products with low stock
     */
    public List<StockLevelClass> getLowStockProducts(String tenantId, Integer threshold) {
        return stockLevelRepository.findLowStockProducts(tenantId, threshold);
    }
    
    /**
     * Update stock level directly (for manual adjustments)
     */
    @Transactional
    public void updateStockLevel(String tenantId, String productId, Integer newQuantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
        
        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            stockLevel.setQuantity(newQuantity);
            stockLevelRepository.save(stockLevel);
        } else {
            // Create new stock level if it doesn't exist
            String stockLevelId = java.util.UUID.randomUUID().toString();
            StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, newQuantity);
            stockLevelRepository.save(stockLevel);
        }
    }
}
