package com.elevate.insc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.repository.ProductClassRepo;

@Service
public class InventoryService {

    private ProductClassRepo productClassRepo;
    private StockLevelService stockLevelService;

    @Autowired
    public InventoryService(ProductClassRepo productClassRepo, StockLevelService stockLevelService) {
        this.productClassRepo = productClassRepo;
        this.stockLevelService = stockLevelService;
    }

    public ApiResponse<?> createNewProduct(List<ProductClass> product) {
        List<ProductClass> newProduct = productClassRepo.saveAll(product);
        for (ProductClass product1 : newProduct) {
            // Create initial stock level (quantity = 0) for each new product
            stockLevelService.createInitialStockLevel(product1.getTenantId(), product1.getId());
        }
        return new ApiResponse<>("Product created successfully",200,newProduct);
    }

    public ApiResponse<?> returnAllProducts() {
        List<ProductClass> allProducts = productClassRepo.findAll();
        return new ApiResponse<>("All products returned successfully",200,allProducts);
    }

    public ApiResponse<?> returnAllProductStock() {
        // This method should be updated to use the new StockLevelService
        // For now, return a message indicating it needs to be updated
        return new ApiResponse<>("This method needs to be updated to use StockLevelService",200,null);
    }

    public void addStock(PurchaseOrderClass order) {
        List<PurchaseOrderItemClass> items = order.getItems();
        for(PurchaseOrderItemClass item : items) {
            ProductClass product = item.getProduct();
            // Use the new StockLevelService to increase stock
            stockLevelService.increaseStock(order.getTenantId(), product.getId(), item.getQuantity());
        }
    }

    public void deductStock(List<InvoiceItemReqDTO> items) {
        for (InvoiceItemReqDTO item : items) {
            Optional<ProductClass> product = productClassRepo.findById(item.getProductId());
            if (product.isPresent()) {
                // Use the new StockLevelService to decrease stock
                stockLevelService.decreaseStock(product.get().getTenantId(), item.getProductId(), item.getQuantity());
            }
        }
    }
}
