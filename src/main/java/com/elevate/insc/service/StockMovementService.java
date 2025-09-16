package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.entity.StockMovementClass;
import com.elevate.insc.repository.StockMovementRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepo stockMovementRepo;

    public StockMovementService(StockMovementRepo stockMovementRepo) {
        this.stockMovementRepo = stockMovementRepo;
    }

//    public ApiResponse<?> recordMovement(Long productId, StockMovementClass.MovementType type,
//                                      Integer quantity, String reference) {
//        StockMovementClass movement = new StockMovementClass();
//        movement.setProductId(productId);
//        movement.setType(type);
//        movement.setQuantity(quantity);
//        movement.setDate(LocalDateTime.now());
//        movement.setReference(reference);
//
//        repository.save(movement);
//        return new ApiResponse<>("Stock movement recorded successfully",200,null);
//    }
//
//    public ApiResponse<?> getMovementsForProduct(Long productId) {
//        List<StockMovementClass> movements = repository.findByProductId(productId);
//        return new ApiResponse<>("Stock movement fetched successfully",200,movements);
//    }

    public ApiResponse<?> recordStockMoment(PurchaseOrderClass order) {
        List<PurchaseOrderItemClass> items = order.getItems();
        for (PurchaseOrderItemClass item : items) {
            StockMovementClass newStockMovement = new StockMovementClass(
                    item.getProductId(),
                    StockMovementClass.MovementType.IN,
                    item.getQuantity(),
                    LocalDateTime.now()
            );
            stockMovementRepo.save(newStockMovement);
        }
        return new ApiResponse<>("Stock movement added successfully",200,null);
    }
}
