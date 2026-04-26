package com.elevate.insc.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public void recordStockMovementsForInvoice(InvoiceClass invoice) {
        if (invoice == null || invoice.getItems() == null || invoice.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice and its items cannot be null or empty");
        }

        for (var item : invoice.getItems()) {
            String movementId = java.util.UUID.randomUUID().toString();
            StockMovementClass movement = new StockMovementClass(
                movementId,
                invoice.getTenantId(),
                item.getProduct().getId(),
                null,
                String.valueOf(invoice.getInvoiceId()),
                StockMovementClass.Type.OUT,
                item.getQuantity(),
                "Invoice: " + invoice.getInvoiceId()
            );
            stockMovementRepository.save(movement);
        }
    }

    @Transactional
    public void recordStockMovementsForPurchaseOrder(PurchaseOrderClass purchaseOrder) {
        if (purchaseOrder == null || purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase order and its items cannot be null or empty");
        }

        for (var item : purchaseOrder.getItems()) {
            String movementId = java.util.UUID.randomUUID().toString();
            StockMovementClass movement = new StockMovementClass(
                movementId,
                purchaseOrder.getTenantId(),
                item.getProduct().getId(),
                purchaseOrder.getId(),
                null,
                StockMovementClass.Type.IN,
                item.getQuantity(),
                "Purchase Order: " + purchaseOrder.getId()
            );
            stockMovementRepository.save(movement);
        }
    }

    @Transactional
    public void recordStockMovementForInvoice(String tenantId, String productId, String invoiceId, Integer quantity, String reference) {
        String movementId = java.util.UUID.randomUUID().toString();
        StockMovementClass movement = new StockMovementClass(
            movementId, tenantId, productId, null, invoiceId,
            StockMovementClass.Type.OUT, quantity,
            reference != null ? reference : "Invoice: " + invoiceId
        );
        stockMovementRepository.save(movement);
    }

    @Transactional
    public void recordStockMovementForPurchaseOrder(String tenantId, String productId, String purchaseOrderId, Integer quantity, String reference) {
        String movementId = java.util.UUID.randomUUID().toString();
        StockMovementClass movement = new StockMovementClass(
            movementId, tenantId, productId, purchaseOrderId, null,
            StockMovementClass.Type.IN, quantity,
            reference != null ? reference : "Purchase Order: " + purchaseOrderId
        );
        stockMovementRepository.save(movement);
    }

    // --- Paged queries (for controllers) ---

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByTenantPaged(String tenantId, Pageable pageable) {
        return stockMovementRepository.findByTenantIdWithProduct(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByProductPaged(String tenantId, String productId, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndProductIdWithProduct(tenantId, productId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByPurchaseOrderPaged(String tenantId, String purchaseOrderId, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndPurchaseOrderIdWithProduct(tenantId, purchaseOrderId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByInvoicePaged(String tenantId, String invoiceId, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndInvoiceIdWithProduct(tenantId, invoiceId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByTypePaged(String tenantId, StockMovementClass.Type type, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndTypeWithProduct(tenantId, type, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementClass> getStockMovementsByDateRangePaged(String tenantId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return stockMovementRepository.findByTenantIdAndDateBetweenWithProduct(tenantId, startDate, endDate, pageable);
    }

    @Transactional
    public void saveMovement(StockMovementClass movement) {
        stockMovementRepository.save(movement);
    }

    // --- Legacy non-paged queries (used by other services) ---

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByProduct(String tenantId, String productId) {
        return stockMovementRepository.findByTenantIdAndProductId(tenantId, productId);
    }

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByTenant(String tenantId) {
        return stockMovementRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByPurchaseOrder(String tenantId, String purchaseOrderId) {
        return stockMovementRepository.findByTenantIdAndPurchaseOrderId(tenantId, purchaseOrderId);
    }

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByInvoice(String tenantId, String invoiceId) {
        return stockMovementRepository.findByTenantIdAndInvoiceId(tenantId, invoiceId);
    }

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByType(String tenantId, StockMovementClass.Type type) {
        return stockMovementRepository.findByTenantIdAndType(tenantId, type);
    }

    @Transactional(readOnly = true)
    public List<StockMovementClass> getStockMovementsByDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Integer getNetStockMovement(String tenantId, String productId) {
        return stockMovementRepository.getNetStockMovementByTenantAndProduct(tenantId, productId);
    }

    @Transactional(readOnly = true)
    public Integer getTotalInMovements(String tenantId, String productId) {
        return stockMovementRepository.getTotalInMovementsByTenantAndProduct(tenantId, productId);
    }

    @Transactional(readOnly = true)
    public Integer getTotalOutMovements(String tenantId, String productId) {
        return stockMovementRepository.getTotalOutMovementsByTenantAndProduct(tenantId, productId);
    }
}
