package com.elevate.insc.service;

import com.elevate.config.InsufficientStockException;
import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.entity.WarehouseClass;
import com.elevate.insc.repository.StockLevelRepository;
import com.elevate.insc.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;

@Service
public class StockLevelService {

    private final StockLevelRepository stockLevelRepository;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public StockLevelService(StockLevelRepository stockLevelRepository, WarehouseRepository warehouseRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void createInitialStockLevel(String tenantId, String productId) {
        // Use default warehouse
        Optional<WarehouseClass> defaultWarehouse = warehouseRepository.findByTenantIdAndIsDefaultTrue(tenantId);
        String warehouseId = defaultWarehouse.map(WarehouseClass::getId).orElse(null);

        if (warehouseId != null && stockLevelRepository.existsByTenantIdAndProductIdAndWarehouseId(tenantId, productId, warehouseId)) {
            return;
        }

        String stockLevelId = java.util.UUID.randomUUID().toString();
        StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, warehouseId, 0);
        stockLevelRepository.save(stockLevel);
    }

    @Transactional
    public void createInitialStockLevel(String tenantId, String productId, String warehouseId) {
        if (stockLevelRepository.existsByTenantIdAndProductIdAndWarehouseId(tenantId, productId, warehouseId)) {
            return;
        }
        String stockLevelId = java.util.UUID.randomUUID().toString();
        StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, warehouseId, 0);
        stockLevelRepository.save(stockLevel);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    public void increaseStock(String tenantId, String productId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);

        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            stockLevel.setQuantity(stockLevel.getQuantity() + quantity);
            stockLevelRepository.save(stockLevel);
        } else {
            String warehouseId = getDefaultWarehouseId(tenantId);
            String stockLevelId = java.util.UUID.randomUUID().toString();
            StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, warehouseId, quantity);
            stockLevelRepository.save(stockLevel);
        }
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    public void increaseStock(String tenantId, String productId, String warehouseId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductIdAndWarehouseId(tenantId, productId, warehouseId);

        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            stockLevel.setQuantity(stockLevel.getQuantity() + quantity);
            stockLevelRepository.save(stockLevel);
        } else {
            String stockLevelId = java.util.UUID.randomUUID().toString();
            StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, warehouseId, quantity);
            stockLevelRepository.save(stockLevel);
        }
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    public boolean decreaseStock(String tenantId, String productId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);

        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            int currentQuantity = stockLevel.getQuantity();

            if (currentQuantity < quantity) {
                throw new InsufficientStockException(productId, currentQuantity, quantity);
            }

            stockLevel.setQuantity(currentQuantity - quantity);
            stockLevelRepository.save(stockLevel);
            return true;
        }
        return false;
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    public boolean decreaseStock(String tenantId, String productId, String warehouseId, Integer quantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductIdAndWarehouseId(tenantId, productId, warehouseId);

        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            int currentQuantity = stockLevel.getQuantity();

            if (currentQuantity < quantity) {
                throw new InsufficientStockException(productId, currentQuantity, quantity);
            }

            stockLevel.setQuantity(currentQuantity - quantity);
            stockLevelRepository.save(stockLevel);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Integer getCurrentStock(String tenantId, String productId) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
        return stockLevelOpt.map(StockLevelClass::getQuantity).orElse(0);
    }

    @Transactional(readOnly = true)
    public boolean hasSufficientStock(String tenantId, String productId, Integer requiredQuantity) {
        Integer currentStock = getCurrentStock(tenantId, productId);
        return currentStock >= requiredQuantity;
    }

    @Transactional(readOnly = true)
    public Page<StockLevelClass> getStockLevelsByTenant(String tenantId, Pageable pageable) {
        return stockLevelRepository.findByTenantIdWithProduct(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockLevelClass> getStockLevelsByWarehouse(String tenantId, String warehouseId, Pageable pageable) {
        return stockLevelRepository.findByTenantIdAndWarehouseIdWithProduct(tenantId, warehouseId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<StockLevelClass> getStockLevel(String tenantId, String productId) {
        return stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);
    }

    @Transactional(readOnly = true)
    public Page<StockLevelClass> getLowStockProducts(String tenantId, Integer threshold, Pageable pageable) {
        return stockLevelRepository.findLowStockProducts(tenantId, threshold, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockLevelClass> getLowStockByReorderPoint(String tenantId, Pageable pageable) {
        return stockLevelRepository.findLowStockByReorderPoint(tenantId, pageable);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
    public void updateStockLevel(String tenantId, String productId, Integer newQuantity) {
        Optional<StockLevelClass> stockLevelOpt = stockLevelRepository.findByTenantIdAndProductId(tenantId, productId);

        if (stockLevelOpt.isPresent()) {
            StockLevelClass stockLevel = stockLevelOpt.get();
            stockLevel.setQuantity(newQuantity);
            stockLevelRepository.save(stockLevel);
        } else {
            String warehouseId = getDefaultWarehouseId(tenantId);
            String stockLevelId = java.util.UUID.randomUUID().toString();
            StockLevelClass stockLevel = new StockLevelClass(stockLevelId, tenantId, productId, warehouseId, newQuantity);
            stockLevelRepository.save(stockLevel);
        }
    }

    private String getDefaultWarehouseId(String tenantId) {
        return warehouseRepository.findByTenantIdAndIsDefaultTrue(tenantId)
                .map(WarehouseClass::getId)
                .orElse(null);
    }
}
