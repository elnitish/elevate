package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.entity.InvoiceClass;
import com.elevate.fna.entity.InvoiceItemsClass;
import com.elevate.insc.entity.ProductClass;
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

    public ApiResponse<?> recordStockMomentForInvoices(InvoiceClass invoice) {
        List<InvoiceItemsClass> items = invoice.getItems();

        for (InvoiceItemsClass item : items) {
            StockMovementClass newStockMovement = new StockMovementClass(
                    item.getProduct().getId(),
                    StockMovementClass.MovementType.OUT,
                    item.getQuantity(),
                    LocalDateTime.now(),
                    "This is the stock movement from Invoice "+invoice.getInvoiceId()
            );
            stockMovementRepo.save(newStockMovement);
        }
        return new ApiResponse<>("Stock movement added successfully",200,null);
    }
//
//    public ApiResponse<?> getMovementsForProduct(Long productId) {
//        List<StockMovementClass> movements = repository.findByProductId(productId);
//        return new ApiResponse<>("Stock movement fetched successfully",200,movements);
//    }

    public ApiResponse<?> recordStockMomentForPurchaseOrder(PurchaseOrderClass order) {
        List<PurchaseOrderItemClass> items = order.getItems();

        for (PurchaseOrderItemClass item : items) {
            StockMovementClass newStockMovement = new StockMovementClass(
                    item.getProductId(),
                    StockMovementClass.MovementType.IN,
                    item.getQuantity(),
                    LocalDateTime.now(),
                    "This is the stock movement from purchase order "+item.getId()
            );
            stockMovementRepo.save(newStockMovement);
        }
        return new ApiResponse<>("Stock movement added successfully",200,null);
    }
    public ApiResponse<?> returnAllStockMovements() {
        List<StockMovementClass> items = stockMovementRepo.findAll();
        return new ApiResponse<>("Stock movement returned successfully",200,items);
    }
}
