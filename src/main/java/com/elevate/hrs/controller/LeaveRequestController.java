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
import com.elevate.hrs.dto.LeaveRequestReqDTO;
import com.elevate.hrs.dto.LeaveRequestResDTO;
import com.elevate.hrs.service.LeaveRequestService;

@RestController
@RequestMapping("/hr/leaves")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createLeaveRequest(
            HttpServletRequest request,
            @Valid @RequestBody LeaveRequestReqDTO leaveRequestReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        String createdBy = (String) request.getAttribute("username");
        
        try {
            LeaveRequestResDTO createdLeaveRequest = leaveRequestService.createLeaveRequest(tenantId, leaveRequestReqDTO, createdBy);
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request created successfully",
                HttpStatus.CREATED.value(),
                createdLeaveRequest
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
    public ResponseEntity<ApiResponse<?>> getAllLeaveRequests(HttpServletRequest request) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<LeaveRequestResDTO> leaveRequests = leaveRequestService.getAllLeaveRequests(tenantId);
        ApiResponse<?> response = new ApiResponse<>(
            "Leave requests retrieved successfully",
            HttpStatus.OK.value(),
            leaveRequests
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{leaveRequestId}")
    public ResponseEntity<ApiResponse<?>> getLeaveRequestById(HttpServletRequest request, @PathVariable Long leaveRequestId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        Optional<LeaveRequestResDTO> leaveRequest = leaveRequestService.getLeaveRequestById(tenantId, leaveRequestId);
        if (leaveRequest.isPresent()) {
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request retrieved successfully",
                HttpStatus.OK.value(),
                leaveRequest.get()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Leave request not found",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<?>> getLeaveRequestsByEmployee(HttpServletRequest request, @PathVariable Long employeeId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<LeaveRequestResDTO> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(tenantId, employeeId);
        ApiResponse<?> response = new ApiResponse<>(
            "Leave requests retrieved successfully",
            HttpStatus.OK.value(),
            leaveRequests
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public ResponseEntity<ApiResponse<?>> getLeaveRequestsByEmployeeAndStatus(
            HttpServletRequest request,
            @PathVariable Long employeeId,
            @PathVariable String status) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<LeaveRequestResDTO> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeAndStatus(tenantId, employeeId, status);
        ApiResponse<?> response = new ApiResponse<>(
            "Leave requests retrieved successfully",
            HttpStatus.OK.value(),
            leaveRequests
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<ApiResponse<?>> getLeaveRequestsByDateRange(
            HttpServletRequest request,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        List<LeaveRequestResDTO> leaveRequests = leaveRequestService.getLeaveRequestsByDateRange(tenantId, startDate, endDate);
        ApiResponse<?> response = new ApiResponse<>(
            "Leave requests retrieved successfully",
            HttpStatus.OK.value(),
            leaveRequests
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{leaveRequestId}")
    public ResponseEntity<ApiResponse<?>> updateLeaveRequest(
            HttpServletRequest request,
            @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveRequestReqDTO leaveRequestReqDTO) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            LeaveRequestResDTO updatedLeaveRequest = leaveRequestService.updateLeaveRequest(tenantId, leaveRequestId, leaveRequestReqDTO);
            if (updatedLeaveRequest != null) {
                ApiResponse<?> response = new ApiResponse<>(
                    "Leave request updated successfully",
                    HttpStatus.OK.value(),
                    updatedLeaveRequest
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request not found",
                HttpStatus.NOT_FOUND.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{leaveRequestId}")
    public ResponseEntity<ApiResponse<?>> deleteLeaveRequest(HttpServletRequest request, @PathVariable Long leaveRequestId) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        try {
            boolean deleted = leaveRequestService.deleteLeaveRequest(tenantId, leaveRequestId);
            if (deleted) {
                ApiResponse<?> response = new ApiResponse<>(
                    "Leave request deleted successfully",
                    HttpStatus.OK.value(),
                    null
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request not found",
                HttpStatus.NOT_FOUND.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ApiResponse<?> response = new ApiResponse<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                null
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{leaveRequestId}/approve")
    public ResponseEntity<ApiResponse<?>> approveLeaveRequest(
            HttpServletRequest request,
            @PathVariable Long leaveRequestId,
            @RequestParam String approvedBy,
            @RequestParam(required = false) String comments) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        LeaveRequestResDTO approvedLeaveRequest = leaveRequestService.approveLeaveRequest(tenantId, leaveRequestId, approvedBy, comments);
        if (approvedLeaveRequest != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request approved successfully",
                HttpStatus.OK.value(),
                approvedLeaveRequest
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Leave request not found or cannot be approved",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{leaveRequestId}/reject")
    public ResponseEntity<ApiResponse<?>> rejectLeaveRequest(
            HttpServletRequest request,
            @PathVariable Long leaveRequestId,
            @RequestParam String approvedBy,
            @RequestParam(required = false) String comments) {
        String tenantId = (String) request.getAttribute("tenantID");
        
        LeaveRequestResDTO rejectedLeaveRequest = leaveRequestService.rejectLeaveRequest(tenantId, leaveRequestId, approvedBy, comments);
        if (rejectedLeaveRequest != null) {
            ApiResponse<?> response = new ApiResponse<>(
                "Leave request rejected successfully",
                HttpStatus.OK.value(),
                rejectedLeaveRequest
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        ApiResponse<?> response = new ApiResponse<>(
            "Leave request not found or cannot be rejected",
            HttpStatus.NOT_FOUND.value(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
