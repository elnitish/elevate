package com.elevate.insc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elevate.insc.dto.SupplierClassReqDTO;
import com.elevate.insc.entity.SupplierClass;
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
    public ApiResponse<?> createNewPurchaseOrder(String tenantID, PurchaseOrderReqDTO purchaseOrderReqDTO) {
        // Validate input
        if (purchaseOrderReqDTO.getItems() == null || purchaseOrderReqDTO.getItems().isEmpty()) {
            return new ApiResponse<>("Purchase order must contain at least one item", 400, null);
        }
        
        // Validate supplier exists and belongs to tenant
        if (!supplierRepository.existsByTenantIdAndId(tenantID, purchaseOrderReqDTO.getSupplierId())) {
            return new ApiResponse<>("Supplier not found", 404, null);
        }

        // Generate UUID for purchase order
        String purchaseOrderId = UUID.randomUUID().toString();
        
        // Set default order date if not provided
        LocalDate orderDate = purchaseOrderReqDTO.getOrderDate() != null ? 
            purchaseOrderReqDTO.getOrderDate() : LocalDate.now();
        
        // Create purchase order entity
        PurchaseOrderClass newPurchaseOrder = new PurchaseOrderClass(
            purchaseOrderId,
            tenantID, // Use session tenantID instead of DTO tenantId
            purchaseOrderReqDTO.getSupplierId(),
            orderDate,
            PurchaseOrderClass.Status.valueOf(purchaseOrderReqDTO.getStatus().toUpperCase())
        );
        
        // Create and add items
        for (var itemDTO : purchaseOrderReqDTO.getItems()) {
            // Validate product exists
            Optional<ProductClass> productOpt = productClassRepo.findById(itemDTO.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Product not found: " + itemDTO.getProductId(), 404, null);
            }
            ProductClass product = productOpt.get();
            
            String itemId = UUID.randomUUID().toString();
            PurchaseOrderItemClass item = new PurchaseOrderItemClass(
                itemId,
                tenantID, // Use session tenantID instead of DTO tenantId
                newPurchaseOrder,
                product,
                itemDTO.getQuantity(),
                itemDTO.getUnitPrice()
            );
            
            newPurchaseOrder.getItems().add(item);
        }
        
        // Calculate and set the total amount on the purchase order
        newPurchaseOrder.setTotalAmount(newPurchaseOrder.calculateTotalAmount());
        
        // Save purchase order first
        PurchaseOrderClass savedPurchaseOrder = purchaseOrderRepository.save(newPurchaseOrder);
        
        // Record stock movements for all items (IN movements)
        stockMovementService.recordStockMovementsForPurchaseOrder(savedPurchaseOrder);
        
        // Increase stock levels for all items
        for (var itemDTO : purchaseOrderReqDTO.getItems()) {
            stockLevelService.increaseStock(
                tenantID, // Use session tenantID
                itemDTO.getProductId(), 
                itemDTO.getQuantity()
            );
        }
        
        PurchaseOrderResDTO responseDTO = new PurchaseOrderResDTO(savedPurchaseOrder);
        
        return new ApiResponse<>("Purchase order created successfully", 201, responseDTO);
    }
    
    public ApiResponse<?> returnAllPurchaseOrders(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            return new ApiResponse<>("Tenant not found", 404, null);
        }
        
        List<PurchaseOrderClass> purchaseOrders = purchaseOrderRepository.findByTenantId(tenantId);
        List<PurchaseOrderResDTO> purchaseOrderDTOs = purchaseOrders.stream()
                .map(PurchaseOrderResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Purchase orders retrieved successfully", 200, purchaseOrderDTOs);
    }
    
    public ApiResponse<?> returnPurchaseOrderById(String tenantId, String purchaseOrderId) {
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
    
    public ApiResponse<?> returnPurchaseOrdersBySupplier(String tenantId, String supplierId) {
        List<PurchaseOrderClass> purchaseOrders = purchaseOrderRepository.findByTenantIdAndSupplierId(tenantId, supplierId);
        List<PurchaseOrderResDTO> purchaseOrderDTOs = purchaseOrders.stream()
                .map(PurchaseOrderResDTO::new)
                .collect(Collectors.toList());
        
        return new ApiResponse<>("Purchase orders retrieved successfully", 200, purchaseOrderDTOs);
    }
    
    public ApiResponse<?> returnPurchaseOrdersByStatus(String tenantId, String status) {
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

    public ApiResponse<?> createNewSupplier(String tenantID, SupplierClassReqDTO supplierClassReqDTO) {
        String UUID = java.util.UUID.randomUUID().toString();
        SupplierClass supplierClass = new SupplierClass(
                UUID,
                tenantID,
                supplierClassReqDTO.getName(),
                supplierClassReqDTO.getEmail(),
                supplierClassReqDTO.getPhone(),
                supplierClassReqDTO.getAddress()
        );
        SupplierClass newSupplier =  supplierRepository.save(supplierClass);
        return new ApiResponse<>("Supplier created successfully", 201, newSupplier);
    }
}