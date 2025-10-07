package com.elevate.insc.service;

import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    
    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }
    
    /**
     * Record stock movement for purchase order (IN)
     */
    @Transactional
    public void recordStockMovementForPurchaseOrder(String tenantId, String productId, String purchaseOrderId, Integer quantity, String reference) {
        String movementId = java.util.UUID.randomUUID().toString();
        StockMovementClass movement = new StockMovementClass(
            movementId,
            tenantId,
            productId,
            purchaseOrderId,
            null, // invoiceId is null for purchase orders
            StockMovementClass.Type.IN,
            quantity,
            reference
        );
        stockMovementRepository.save(movement);
    }
    
    /**
     * Record stock movement for invoice (OUT)
     */
    @Transactional
    public void recordStockMovementForInvoice(String tenantId, String productId, Long invoiceId, Integer quantity, String reference) {
        String movementId = java.util.UUID.randomUUID().toString();
        StockMovementClass movement = new StockMovementClass(
            movementId,
            tenantId,
            productId,
            null, // purchaseOrderId is null for invoices
            invoiceId,
            StockMovementClass.Type.OUT,
            quantity,
            reference
        );
        stockMovementRepository.save(movement);
    }
    
    /**
     * Record stock movement for purchase order items
     */
    @Transactional
    public void recordStockMovementsForPurchaseOrder(String tenantId, String purchaseOrderId, List<com.elevate.insc.dto.PurchaseOrderItemReqDTO> items) {
        for (com.elevate.insc.dto.PurchaseOrderItemReqDTO item : items) {
            recordStockMovementForPurchaseOrder(
                tenantId,
                item.getProductId(),
                purchaseOrderId,
                item.getQuantity(),
                "Purchase Order: " + purchaseOrderId
            );
        }
    }
    
    /**
     * Record stock movements for invoice items
     */
    @Transactional
    public void recordStockMovementsForInvoice(String tenantId, Long invoiceId, List<com.elevate.fna.dto.InvoiceItemReqDTO> items) {
        for (com.elevate.fna.dto.InvoiceItemReqDTO item : items) {
            recordStockMovementForInvoice(
                tenantId,
                item.getProductId(),
                invoiceId,
                item.getQuantity(),
                "Invoice: " + invoiceId
            );
        }
    }
    
    /**
     * Get stock movements for a product
     */
    public List<StockMovementClass> getStockMovementsByProduct(String tenantId, String productId) {
        return stockMovementRepository.findByTenantIdAndProductId(tenantId, productId);
    }
    
    /**
     * Get stock movements for a tenant
     */
    public List<StockMovementClass> getStockMovementsByTenant(String tenantId) {
        return stockMovementRepository.findByTenantId(tenantId);
    }
    
    /**
     * Get stock movements for a purchase order
     */
    public List<StockMovementClass> getStockMovementsByPurchaseOrder(String tenantId, String purchaseOrderId) {
        return stockMovementRepository.findByTenantIdAndPurchaseOrderId(tenantId, purchaseOrderId);
    }
    
    /**
     * Get stock movements for an invoice
     */
    public List<StockMovementClass> getStockMovementsByInvoice(String tenantId, Long invoiceId) {
        return stockMovementRepository.findByTenantIdAndInvoiceId(tenantId, invoiceId);
    }
    
    /**
     * Get stock movements by type (IN/OUT)
     */
    public List<StockMovementClass> getStockMovementsByType(String tenantId, StockMovementClass.Type type) {
        return stockMovementRepository.findByTenantIdAndType(tenantId, type);
    }
    
    /**
     * Get stock movements by date range
     */
    public List<StockMovementClass> getStockMovementsByDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);
    }
    
    /**
     * Get net stock movement for a product (IN - OUT)
     */
    public Integer getNetStockMovement(String tenantId, String productId) {
        return stockMovementRepository.getNetStockMovementByTenantAndProduct(tenantId, productId);
    }
    
    /**
     * Get total IN movements for a product
     */
    public Integer getTotalInMovements(String tenantId, String productId) {
        return stockMovementRepository.getTotalInMovementsByTenantAndProduct(tenantId, productId);
    }
    
    /**
     * Get total OUT movements for a product
     */
    public Integer getTotalOutMovements(String tenantId, String productId) {
        return stockMovementRepository.getTotalOutMovementsByTenantAndProduct(tenantId, productId);
    }
}