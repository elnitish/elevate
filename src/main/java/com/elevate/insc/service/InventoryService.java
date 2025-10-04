package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.fna.dto.InvoiceItemReqDTO;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.PurchaseOrderClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import com.elevate.insc.entity.StockLevelClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.insc.repository.PurchaseOrderItemRepo;
import com.elevate.insc.repository.StockLevelRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private ProductClassRepo productClassRepo;
    private StockLevelRepo stockLevelRepo;

    @Autowired
    public InventoryService(ProductClassRepo productClassRepo, StockLevelRepo stockLevelRepo, PurchaseOrderItemRepo purchaseOrderItemRepo,StockMovementService stockMovementService) {
        this.productClassRepo = productClassRepo;
        this.stockLevelRepo = stockLevelRepo;
    }

    public ApiResponse<?> createNewProduct(List<ProductClass> product) {
        List<ProductClass> newProduct = productClassRepo.saveAll(product);
        for (ProductClass product1 : newProduct) {
            StockLevelClass stock = new StockLevelClass();
            stock.setProduct(product1);
            stock.setQuantity(0);
            stockLevelRepo.save(stock);
        }
        return new ApiResponse<>("Product created successfully",200,newProduct);
    }

    public ApiResponse<?> returnAllProducts() {
        List<ProductClass> allProducts = productClassRepo.findAll();
        return new ApiResponse<>("All products returned successfully",200,allProducts);
    }

    public ApiResponse<?> returnAllProductStock() {
        return new ApiResponse<>("All stock levels returned successfully",200,stockLevelRepo.findAll());
    }

    public void addStock(PurchaseOrderClass order) {
        List<PurchaseOrderItemClass> items = order.getItems();
        for(PurchaseOrderItemClass item : items) {
            Optional<ProductClass> product =  productClassRepo.findById(item.getProductId());
            StockLevelClass stock = stockLevelRepo.findByProduct(product.get()).orElse(null);
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockLevelRepo.save(stock);
        }
    }

    public void deductStock(List<InvoiceItemReqDTO> items) {
//        for (InvoiceItemReqDTO item : items) {
//            Optional<ProductClass> product = productClassRepo.findById(item.getProductId());
//            System.out.println(product.get());
//            StockLevelClass stock = stockLevelRepo.findByProduct(product.get()).orElse(null);
//            stock.setQuantity(stock.getQuantity() - item.getQuantity());
//            stockLevelRepo.save(stock);
//        }
    }
}
