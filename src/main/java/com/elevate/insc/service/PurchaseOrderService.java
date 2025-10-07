package com.elevate.insc.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.repository.TenantRepository;
import com.elevate.insc.dto.PurchaseOrderReqDTO;
import com.elevate.insc.dto.PurchaseOrderResDTO;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.repository.PurchaseOrderRepository;
import com.elevate.insc.repository.SupplierRepository;
import com.elevate.insc.service.StockLevelService;
import com.elevate.insc.service.StockMovementService;

@Service
public class PurchaseOrderService {
    
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductClassRepo productClassRepo;
    private final SupplierRepository supplierRepository;
    private final TenantRepository tenantRepository;
    private final StockLevelService stockLevelService;
    private final StockMovementService stockMovementService;
    
    @Autowired
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                               ProductClassRepo productClassRepo,
                               SupplierRepository supplierRepository,
                               TenantRepository tenantRepository,
                               StockLevelService stockLevelService,
                               StockMovementService stockMovementService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productClassRepo = productClassRepo;
        this.supplierRepository = supplierRepository;
        this.tenantRepository = tenantRepository;
        this.stockLevelService = stockLevelService;
        this.stockMovementService = stockMovementService;
    }
    
    @Transactional
    public ApiResponse<?> createPurchaseOrder(PurchaseOrderReqDTO purchaseOrderReqDTO) {
        // Validate tenant exists
        if (!tenantRepository.existsById(purchaseOrderReqDTO.getTenantId())) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        // Validate supplier exists and belongs to tenant
        if (!supplierRepository.existsByTenantIdAndId(purchaseOrderReqDTO.getTenantId(), purchaseOrderReqDTO.getSupplierId())) {
            return new ApiResponse<>("Supplier not found or does not belong to this tenant", 404, null);
        }
        
        // Validate all products exist and belong to tenant
        for (var itemDTO : purchaseOrderReqDTO.getItems()) {
            Optional<ProductClass> productOpt = productClassRepo.findById(itemDTO.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Product with ID " + itemDTO.getProductId() + " not found", 404, null);
            }
            
            ProductClass product = productOpt.get();
            if (!product.getTenantId().equals(purchaseOrderReqDTO.getTenantId())) {
                return new ApiResponse<>("Product does not belong to this tenant", 403, null);
            }
        }
        
        // Generate UUID for purchase order
        String purchaseOrderId = UUID.randomUUID().toString();
        
        // Set default order date if not provided
        LocalDate orderDate = purchaseOrderReqDTO.getOrderDate() != null ? 
            purchaseOrderReqDTO.getOrderDate() : LocalDate.now();
        
        // Create purchase order entity
        PurchaseOrderClass newPurchaseOrder = new PurchaseOrderClass(
            purchaseOrderId,
            purchaseOrderReqDTO.getTenantId(),
            purchaseOrderReqDTO.getSupplierId(),
            orderDate,
            PurchaseOrderClass.Status.valueOf(purchaseOrderReqDTO.getStatus().toUpperCase())
        );
        
        // Create and add items
        for (var itemDTO : purchaseOrderReqDTO.getItems()) {
            ProductClass product = productClassRepo.findById(itemDTO.getProductId()).get();
            
            String itemId = UUID.randomUUID().toString();
            PurchaseOrderItemClass item = new PurchaseOrderItemClass(
                itemId,
                purchaseOrderReqDTO.getTenantId(),
                newPurchaseOrder,
                product,
                itemDTO.getQuantity(),
                itemDTO.getUnitPrice()
            );
            
            newPurchaseOrder.getItems().add(item);
        }
        
        PurchaseOrderClass savedPurchaseOrder = purchaseOrderRepository.save(newPurchaseOrder);
        
        // Record stock movements for all items (IN movements)
        stockMovementService.recordStockMovementsForPurchaseOrder(
            purchaseOrderReqDTO.getTenantId(), 
            purchaseOrderId, 
            purchaseOrderReqDTO.getItems()
        );
        
        // Increase stock levels for all items
        for (var itemDTO : purchaseOrderReqDTO.getItems()) {
            stockLevelService.increaseStock(
                purchaseOrderReqDTO.getTenantId(), 
                itemDTO.getProductId(), 
                itemDTO.getQuantity()
            );
        }
        
        PurchaseOrderResDTO responseDTO = new PurchaseOrderResDTO(savedPurchaseOrder);
        
        return new ApiResponse<>("Purchase order created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> getPurchaseOrdersByTenant(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<PurchaseOrderClass> purchaseOrders = purchaseOrderRepository.findByTenantId(tenantId);
        List<PurchaseOrderResDTO> purchaseOrderDTOs = purchaseOrders.stream()
                .map(PurchaseOrderResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Purchase orders retrieved successfully", 200, purchaseOrderDTOs);
    }
    
    public ApiResponse<?> getPurchaseOrderById(String tenantId, String purchaseOrderId) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<PurchaseOrderClass> purchaseOrderOpt = purchaseOrderRepository.findByTenantIdAndId(tenantId, purchaseOrderId);
        if (purchaseOrderOpt.isEmpty()) {
            return new ApiResponse<>("Purchase order not found", 404, null);
        }
        
        PurchaseOrderResDTO responseDTO = new PurchaseOrderResDTO(purchaseOrderOpt.get());
        return new ApiResponse<>("Purchase order retrieved successfully", 200, responseDTO);
    }
    
    @Transactional
    public ApiResponse<?> updatePurchaseOrderStatus(String tenantId, String purchaseOrderId, String status) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        Optional<PurchaseOrderClass> purchaseOrderOpt = purchaseOrderRepository.findByTenantIdAndId(tenantId, purchaseOrderId);
        if (purchaseOrderOpt.isEmpty()) {
            return new ApiResponse<>("Purchase order not found", 404, null);
        }
        
        try {
            PurchaseOrderClass.Status newStatus = PurchaseOrderClass.Status.valueOf(status.toUpperCase());
            PurchaseOrderClass purchaseOrder = purchaseOrderOpt.get();
            purchaseOrder.setStatus(newStatus);
            
            PurchaseOrderClass updatedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            PurchaseOrderResDTO responseDTO = new PurchaseOrderResDTO(updatedPurchaseOrder);
            
            return new ApiResponse<>("Purchase order status updated successfully", 200, responseDTO);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>("Invalid status. Must be PENDING, RECEIVED, or CANCELLED", 400, null);
        }
    }
    
    public ApiResponse<?> getPurchaseOrdersBySupplier(String tenantId, String supplierId) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<PurchaseOrderClass> purchaseOrders = purchaseOrderRepository.findByTenantIdAndSupplierId(tenantId, supplierId);
        List<PurchaseOrderResDTO> purchaseOrderDTOs = purchaseOrders.stream()
                .map(PurchaseOrderResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Purchase orders retrieved successfully", 200, purchaseOrderDTOs);
    }
    
    public ApiResponse<?> getPurchaseOrdersByStatus(String tenantId, String status) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        try {
            PurchaseOrderClass.Status orderStatus = PurchaseOrderClass.Status.valueOf(status.toUpperCase());
            List<PurchaseOrderClass> purchaseOrders = purchaseOrderRepository.findByTenantIdAndStatus(tenantId, orderStatus);
            List<PurchaseOrderResDTO> purchaseOrderDTOs = purchaseOrders.stream()
                    .map(PurchaseOrderResDTO::new)
                    .collect(Collectors.toList());
            
            return new ApiResponse<>("Purchase orders retrieved successfully", 200, purchaseOrderDTOs);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>("Invalid status. Must be PENDING, RECEIVED, or CANCELLED", 400, null);
        }
    }
}