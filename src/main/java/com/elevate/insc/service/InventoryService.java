package com.elevate.insc.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private ProductClassRepo productClassRepo;

    @Autowired
    public InventoryService(ProductClassRepo productClassRepo) {
        this.productClassRepo = productClassRepo;

    }

    public ApiResponse<?> createNewProduct(List<ProductClass> product) {
        List<ProductClass> newProduct = productClassRepo.saveAll(product);
        return new ApiResponse<>("Product created successfully",200,newProduct);
    }

    public ApiResponse<?> returnAllProducts() {
        List<ProductClass> allProducts = productClassRepo.findAll();
        return new ApiResponse<>("All products returned successfully",200,allProducts);
    }
}
