package com.elevate.insc.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.fna.entity.InvoiceClass;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.repository.StockMovementRepository;

@Service
public class StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    
    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }
    
    /**
     * Record stock movements for invoice items
     * Creates OUT type stock movements when products are sold to customers
     * 
     * @param invoice The invoice containing items to process
     */
    @Transactional
    public void recordStockMovementsForInvoice(InvoiceClass invoice) {
        try {
            // Validate input
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice cannot be null");
            }
            if (invoice.getTenantId() == null || invoice.getTenantId().trim().isEmpty()) {
                throw new IllegalArgumentException("Invoice tenant ID cannot be null or empty");
            }
            if (invoice.getInvoiceId() == null || invoice.getInvoiceId().trim().isEmpty()) {
                throw new IllegalArgumentException("Invoice ID cannot be null or empty");
            }
            if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
                throw new IllegalArgumentException("Invoice items cannot be null or empty");
            }
            
            // Process each item in the invoice
            for (var item : invoice.getItems()) {
                if (item.getProduct() == null) {
                    throw new IllegalArgumentException("Product cannot be null for invoice item");
                }
                if (item.getProduct().getId() == null || item.getProduct().getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Product ID cannot be null or empty for invoice item");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive for product: " + item.getProduct().getId());
                }
                
                // Create stock movement record
                String movementId = java.util.UUID.randomUUID().toString();
                StockMovementClass movement = new StockMovementClass(
                    movementId,
                    invoice.getTenantId(),
                    item.getProduct().getId(),
                    null, // purchaseOrderId is null for invoices
                    invoice.getInvoiceId(),
                    StockMovementClass.Type.OUT,
                    item.getQuantity(),
                    "Invoice: " + invoice.getInvoiceId()
                );
                
                stockMovementRepository.save(movement);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to record stock movements for invoice: " + e.getMessage(), e);
        }
    }
    
    /**
     * Record stock movements for purchase order items
     * Creates IN type stock movements when products are received from suppliers
     * 
     * @param purchaseOrder The purchase order containing items to process
     */
    @Transactional
    public void recordStockMovementsForPurchaseOrder(PurchaseOrderClass purchaseOrder) {
        try {
            // Validate input
            if (purchaseOrder == null) {
                throw new IllegalArgumentException("Purchase order cannot be null");
            }
            if (purchaseOrder.getTenantId() == null || purchaseOrder.getTenantId().trim().isEmpty()) {
                throw new IllegalArgumentException("Purchase order tenant ID cannot be null or empty");
            }
            if (purchaseOrder.getId() == null || purchaseOrder.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("Purchase order ID cannot be null or empty");
            }
            if (purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
                throw new IllegalArgumentException("Purchase order items cannot be null or empty");
            }
            
            // Process each item in the purchase order
            for (var item : purchaseOrder.getItems()) {
                if (item.getProduct() == null) {
                    throw new IllegalArgumentException("Product cannot be null for purchase order item");
                }
                if (item.getProduct().getId() == null || item.getProduct().getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Product ID cannot be null or empty for purchase order item");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive for product: " + item.getProduct().getId());
                }
                
                // Create stock movement record
                String movementId = java.util.UUID.randomUUID().toString();
                StockMovementClass movement = new StockMovementClass(
                    movementId,
                    purchaseOrder.getTenantId(),
                    item.getProduct().getId(),
                    purchaseOrder.getId(),
                    null, // invoiceId is null for purchase orders
                    StockMovementClass.Type.IN,
                    item.getQuantity(),
                    "Purchase Order: " + purchaseOrder.getId()
                );
                
                stockMovementRepository.save(movement);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to record stock movements for purchase order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Record individual stock movement for invoice (OUT)
     * Records single stock movement when a product is sold to a customer
     */
    @Transactional
    public void recordStockMovementForInvoice(String tenantId, String productId, String invoiceId, Integer quantity, String reference) {
        try {
            // Validate input parameters
            if (tenantId == null || tenantId.trim().isEmpty()) {
                throw new IllegalArgumentException("Tenant ID cannot be null or empty");
            }
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            if (invoiceId == null || invoiceId.trim().isEmpty()) {
                throw new IllegalArgumentException("Invoice ID cannot be null or empty");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            String movementId = java.util.UUID.randomUUID().toString();
            StockMovementClass movement = new StockMovementClass(
                movementId,
                tenantId,
                productId,
                null, // purchaseOrderId is null for invoices
                invoiceId,
                StockMovementClass.Type.OUT,
                quantity,
                reference != null ? reference : "Invoice: " + invoiceId
            );
            
            stockMovementRepository.save(movement);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to record stock movement for invoice: " + e.getMessage(), e);
        }
    }
    
    /**
     * Record individual stock movement for purchase order (IN)
     * Records single stock movement when a product is received from a supplier
     */
    @Transactional
    public void recordStockMovementForPurchaseOrder(String tenantId, String productId, String purchaseOrderId, Integer quantity, String reference) {
        try {
            // Validate input parameters
            if (tenantId == null || tenantId.trim().isEmpty()) {
                throw new IllegalArgumentException("Tenant ID cannot be null or empty");
            }
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            if (purchaseOrderId == null || purchaseOrderId.trim().isEmpty()) {
                throw new IllegalArgumentException("Purchase Order ID cannot be null or empty");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            String movementId = java.util.UUID.randomUUID().toString();
            StockMovementClass movement = new StockMovementClass(
                movementId,
                tenantId,
                productId,
                purchaseOrderId,
                null, // invoiceId is null for purchase orders
                StockMovementClass.Type.IN,
                quantity,
                reference != null ? reference : "Purchase Order: " + purchaseOrderId
            );
            
            stockMovementRepository.save(movement);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to record stock movement for purchase order: " + e.getMessage(), e);
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
    public List<StockMovementClass> getStockMovementsByInvoice(String tenantId, String invoiceId) {
        // Convert String invoiceId to Long for repository method
        try {
            Long invoiceIdLong = Long.parseLong(invoiceId);
            return stockMovementRepository.findByTenantIdAndInvoiceId(tenantId, invoiceIdLong);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid invoice ID format: " + invoiceId, e);
        }
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