package com.elevate.insc.controller;

import com.elevate.insc.dto.UpdateProductReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.ProductReqDTO;
import com.elevate.insc.service.ProductService;

@RestController
@RequestMapping("/insc")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping("/createProduct")//working
    public ResponseEntity<ApiResponse<?>> createProduct(HttpServletRequest request, @RequestBody ProductReqDTO productReqDTO) {
        ApiResponse<?> response = productService.createNewProduct(productReqDTO, (String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getAllProducts")//working
    public ResponseEntity<ApiResponse<?>> getAllProductsByTenant(HttpServletRequest request) {
        ApiResponse<?> response = productService.returnAllProducts((String)request.getAttribute("tenantID"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/getProductById/{productId}")//working
    public ResponseEntity<ApiResponse<?>> getProductById(HttpServletRequest request,@PathVariable String productId) {
        ApiResponse<?> response = productService.returnProductWithID((String) request.getAttribute("tenantID"),productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/updateProduct")//working
    public ResponseEntity<ApiResponse<?>> updateProduct(HttpServletRequest request, @RequestBody UpdateProductReqDTO updateProductReqDTO) {
        ApiResponse<?> response = productService.updateProductInDB((String) request.getAttribute("tenantID"),updateProductReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/deleteProductById/{productId}")//working
    public ResponseEntity<ApiResponse<?>> deleteProduct(HttpServletRequest request,@PathVariable String productId) {
        ApiResponse<?> response = productService.deleteProductFromDB((String)request.getAttribute("tenantID"),productId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}