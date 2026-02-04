package com.elevate.hrs.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.hrs.dto.PerformanceReviewReqDTO;
import com.elevate.hrs.dto.PerformanceReviewResDTO;
import com.elevate.hrs.service.PerformanceReviewService;

@RestController
@RequestMapping("/hr/performance-reviews")
public class PerformanceReviewController {

    @Autowired
    private PerformanceReviewService performanceReviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPerformanceReview(
            HttpServletRequest request,
            @Valid @RequestBody PerformanceReviewReqDTO performanceReviewReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            PerformanceReviewResDTO createdReview = performanceReviewService.createPerformanceReview(tenantId, performanceReviewReqDTO);
            ApiResponse<?> response = new ApiResponse<>(
                "Performance review created successfully",
                HttpStatus.CREATED.value(),
                createdReview
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllPerformanceReviews(HttpServletRequest request) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PerformanceReviewResDTO> reviews = performanceReviewService.getAllPerformanceReviews(tenantId);
        ApiResponse<?> response = new ApiResponse<>(
            "Performance reviews retrieved successfully",
            HttpStatus.OK.value(),
            reviews
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> getPerformanceReviewById(HttpServletRequest request, @PathVariable Long reviewId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        Optional<PerformanceReviewResDTO> review = performanceReviewService.getPerformanceReviewById(tenantId, reviewId);
        if (review.isPresent()) {
            ApiResponse<?> response = new ApiResponse<>(
                "Performance review retrieved successfully",
                HttpStatus.OK.value(),
                review.get()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Performance review not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<?>> getPerformanceReviewsByEmployee(HttpServletRequest request, @PathVariable Long employeeId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PerformanceReviewResDTO> reviews = performanceReviewService.getPerformanceReviewsByEmployee(tenantId, employeeId);
        ApiResponse<?> response = new ApiResponse<>(
            "Performance reviews retrieved successfully",
            HttpStatus.OK.value(),
            reviews
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse<?>> getPerformanceReviewsByDateRange(
            HttpServletRequest request,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<PerformanceReviewResDTO> reviews = performanceReviewService.getPerformanceReviewsByDateRange(tenantId, startDate, endDate);
        ApiResponse<?> response = new ApiResponse<>(
            "Performance reviews retrieved successfully",
            HttpStatus.OK.value(),
            reviews
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> updatePerformanceReview(
            HttpServletRequest request,
            @PathVariable Long reviewId,
            @Valid @RequestBody PerformanceReviewReqDTO performanceReviewReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        PerformanceReviewResDTO updatedReview = performanceReviewService.updatePerformanceReview(tenantId, reviewId, performanceReviewReqDTO);
        if (updatedReview != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Performance review updated successfully",
                HttpStatus.OK.value(),
                updatedReview
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Performance review not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deletePerformanceReview(HttpServletRequest request, @PathVariable Long reviewId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        boolean deleted = performanceReviewService.deletePerformanceReview(tenantId, reviewId);
        if (deleted) {
            ApiResponse<?> response = new ApiResponse<>(
                "Performance review deleted successfully",
                HttpStatus.OK.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Performance review not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
