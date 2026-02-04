package com.elevate.insc.controller;

import com.elevate.insc.dto.UpdateCategoryReqDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.insc.dto.CategoryReqDTO;
import com.elevate.insc.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping("/createCategory")//working
    public ResponseEntity<ApiResponse<?>> createCategory(HttpServletRequest request, @RequestBody CategoryReqDTO categoryReqDTO) {
        ApiResponse<?> response = categoryService.createCategory((String)request.getAttribute("tenantID"),categoryReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
    
    @GetMapping("getAllCategory")//working
    public ResponseEntity<ApiResponse<?>> getCategoriesByTenant(HttpServletRequest request) {
        ApiResponse<?> response = categoryService.getCategoriesByTenant((String) request.getAttribute("tenantID"));
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }


    @PutMapping("/updateCategory")//working
    public ResponseEntity<ApiResponse<?>> updateCategory(HttpServletRequest request, @RequestBody UpdateCategoryReqDTO updateCategoryReqDTO) {
        ApiResponse<?> response = categoryService.updateCategory((String)request.getAttribute("tenantID"),updateCategoryReqDTO);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @DeleteMapping("/deleteCategory/{categoryId}")//working
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable String categoryId) {
        ApiResponse<?> response = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}