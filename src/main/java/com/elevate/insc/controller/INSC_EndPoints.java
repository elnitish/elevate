package com.elevate.insc.controller;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class INSC_EndPoints {

    private final InventoryService inventoryService;

    @Autowired
    public INSC_EndPoints(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@Valid @RequestBody List<ProductClass> product) {
        ApiResponse<?> response=inventoryService.createNewProduct(product);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        ApiResponse<?> response = inventoryService.returnAllProducts();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}
