package com.elevate.insc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.CategoryReqDTO;
import com.elevate.insc.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody CategoryReqDTO categoryReqDTO) {
        ApiResponse<?> response = categoryService.createCategory(categoryReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<?>> getCategoriesByTenant(@PathVariable String tenantId) {
        ApiResponse<?> response = categoryService.getCategoriesByTenant(tenantId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable String categoryId) {
        ApiResponse<?> response = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable String categoryId, @RequestBody CategoryReqDTO categoryReqDTO) {
        ApiResponse<?> response = categoryService.updateCategory(categoryId, categoryReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable String categoryId) {
        ApiResponse<?> response = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
