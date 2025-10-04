package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.UpdatePurchaseOrderStatusReqDTO;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.repository.PurchaseOrderRepo;
import com.elevate.insc.repository.StockMovementRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepo orderRepository;
    private final StockMovementService stockMovementService;
    private final InventoryService inventoryService;

    public PurchaseOrderService(PurchaseOrderRepo orderRepository, StockMovementService stockMovementService,InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.stockMovementService = stockMovementService;
        this.inventoryService = inventoryService;
    }

    public ApiResponse<?> createOrder(PurchaseOrderClass order) {
        order.setOrderDate(LocalDate.now());
        order.setStatus(PurchaseOrderClass.Status.PENDING);
        if (order.getItems() != null) {
            for (PurchaseOrderItemClass item : order.getItems()) {
                item.setPurchaseOrder(order);
            }
        }
        orderRepository.save(order);
        stockMovementService.recordStockMomentForPurchaseOrder(order);
        inventoryService.addStock(order);
        return new ApiResponse<>("Purchase order created successfully", 200, null);
    }

    public ApiResponse<?> markReceived(UpdatePurchaseOrderStatusReqDTO updatePurchaseOrderStatusReqDTO) {
        Long orderId = updatePurchaseOrderStatusReqDTO.getOrderID();
        String Status = updatePurchaseOrderStatusReqDTO.getNewStatus();
        PurchaseOrderClass order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        PurchaseOrderClass.Status status = PurchaseOrderClass.Status.valueOf(Status);
        order.setStatus(status);
        orderRepository.save(order);
        return new ApiResponse<>("Order marked successfully", 200, null);
    }

    public ApiResponse<?> listOrders() {
        List<PurchaseOrderClass> orders = orderRepository.findAll();
        return new ApiResponse<>("Orders listed", 200, orders);
    }
}
