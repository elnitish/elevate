package com.elevate.insc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.ProductReqDTO;
import com.elevate.insc.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody ProductReqDTO productReqDTO) {
        ApiResponse<?> response = productService.createProduct(productReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> getProductsByTenant(@PathVariable String tenantId) {
        ApiResponse<?> response = productService.getProductsByTenant(tenantId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getProductsByCategory(@PathVariable String tenantId, @PathVariable String categoryId) {
        ApiResponse<?> response = productService.getProductsByCategory(tenantId, categoryId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable String tenantId, @PathVariable String productId) {
        ApiResponse<?> response = productService.getProductById(tenantId, productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/tenant/{tenantId}/product/{productId}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable String tenantId, @PathVariable String productId, @RequestBody ProductReqDTO productReqDTO) {
        ApiResponse<?> response = productService.updateProduct(tenantId, productId, productReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/tenant/{tenantId}/product/{productId}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable String tenantId, @PathVariable String productId) {
        ApiResponse<?> response = productService.deleteProduct(tenantId, productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
