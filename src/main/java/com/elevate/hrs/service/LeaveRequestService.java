package com.elevate.hrs.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.hrs.dto.LeaveRequestReqDTO;
import com.elevate.hrs.dto.LeaveRequestResDTO;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.entity.LeaveRequestClass;
import com.elevate.hrs.repository.EmployeeClassRepo;
import com.elevate.hrs.repository.LeaveRequestRepository;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeClassRepo employeeRepository;

    @Transactional
    public LeaveRequestResDTO createLeaveRequest(String tenantId, LeaveRequestReqDTO leaveRequestReqDTO, String createdBy) {
        // Validate employee exists
        Optional<EmployeeClass> employee = employeeRepository.findById(leaveRequestReqDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new RuntimeException("Employee not found with id: " + leaveRequestReqDTO.getEmployeeId());
        }

        // Validate dates
        if (leaveRequestReqDTO.getEndDate().isBefore(leaveRequestReqDTO.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        LeaveRequestClass leaveRequest = new LeaveRequestClass();
        leaveRequest.setTenantId(tenantId);
        leaveRequest.setEmployee(employee.get());
        leaveRequest.setLeaveType(LeaveRequestClass.LeaveType.valueOf(leaveRequestReqDTO.getLeaveType().toUpperCase()));
        leaveRequest.setStartDate(leaveRequestReqDTO.getStartDate());
        leaveRequest.setEndDate(leaveRequestReqDTO.getEndDate());
        leaveRequest.setReason(leaveRequestReqDTO.getReason());
        
        // Calculate number of days
        long daysDifference = ChronoUnit.DAYS.between(leaveRequestReqDTO.getStartDate(), leaveRequestReqDTO.getEndDate()) + 1;
        leaveRequest.setNumberOfDays((int) daysDifference);

        if (leaveRequestReqDTO.getStatus() != null) {
            try {
                leaveRequest.setStatus(LeaveRequestClass.Status.valueOf(leaveRequestReqDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                leaveRequest.setStatus(LeaveRequestClass.Status.PENDING);
            }
        } else {
            leaveRequest.setStatus(LeaveRequestClass.Status.PENDING);
        }

        LeaveRequestClass savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return convertToResDTO(savedLeaveRequest);
    }

    public List<LeaveRequestResDTO> getAllLeaveRequests(String tenantId) {
        return leaveRequestRepository.findByTenantId(tenantId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestResDTO> getLeaveRequestsByStatus(String tenantId, String status) {
        return leaveRequestRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestResDTO> getLeaveRequestsByEmployee(String tenantId, Long employeeId) {
        return leaveRequestRepository.findByTenantIdAndEmployeeId(tenantId, employeeId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestResDTO> getLeaveRequestsByEmployeeAndStatus(String tenantId, Long employeeId, String status) {
        return leaveRequestRepository.findByTenantIdAndEmployeeIdAndStatus(tenantId, employeeId, status).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestResDTO> getLeaveRequestsByDateRange(String tenantId, LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findByTenantIdAndStartDateBetween(tenantId, startDate, endDate).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public Optional<LeaveRequestResDTO> getLeaveRequestById(String tenantId, Long leaveRequestId) {
        Optional<LeaveRequestClass> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent() && leaveRequest.get().getTenantId().equals(tenantId)) {
            return leaveRequest.map(this::convertToResDTO);
        }
        return Optional.empty();
    }

    @Transactional
    public LeaveRequestResDTO updateLeaveRequest(String tenantId, Long leaveRequestId, LeaveRequestReqDTO leaveRequestReqDTO) {
        Optional<LeaveRequestClass> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent() && leaveRequest.get().getTenantId().equals(tenantId)) {
            LeaveRequestClass leaveRequestClass = leaveRequest.get();
            
            // Only allow updates to pending leave requests
            if (!leaveRequestClass.getStatus().equals(LeaveRequestClass.Status.PENDING)) {
                throw new RuntimeException("Cannot update leave request that is not in PENDING status");
            }

            leaveRequestClass.setLeaveType(LeaveRequestClass.LeaveType.valueOf(leaveRequestReqDTO.getLeaveType().toUpperCase()));
            leaveRequestClass.setStartDate(leaveRequestReqDTO.getStartDate());
            leaveRequestClass.setEndDate(leaveRequestReqDTO.getEndDate());
            leaveRequestClass.setReason(leaveRequestReqDTO.getReason());
            
            // Recalculate number of days
            long daysDifference = ChronoUnit.DAYS.between(leaveRequestReqDTO.getStartDate(), leaveRequestReqDTO.getEndDate()) + 1;
            leaveRequestClass.setNumberOfDays((int) daysDifference);

            LeaveRequestClass updatedLeaveRequest = leaveRequestRepository.save(leaveRequestClass);
            return convertToResDTO(updatedLeaveRequest);
        }
        return null;
    }

    @Transactional
    public boolean deleteLeaveRequest(String tenantId, Long leaveRequestId) {
        Optional<LeaveRequestClass> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent() && leaveRequest.get().getTenantId().equals(tenantId)) {
            // Only allow deletion of pending leave requests
            if (!leaveRequest.get().getStatus().equals(LeaveRequestClass.Status.PENDING)) {
                throw new RuntimeException("Cannot delete leave request that is not in PENDING status");
            }
            leaveRequestRepository.deleteById(leaveRequestId);
            return true;
        }
        return false;
    }

    @Transactional
    public LeaveRequestResDTO approveLeaveRequest(String tenantId, Long leaveRequestId, String approvedBy, String comments) {
        Optional<LeaveRequestClass> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent() && leaveRequest.get().getTenantId().equals(tenantId)) {
            LeaveRequestClass leaveRequestClass = leaveRequest.get();
            if (leaveRequestClass.getStatus().equals(LeaveRequestClass.Status.PENDING)) {
                leaveRequestClass.setStatus(LeaveRequestClass.Status.APPROVED);
                leaveRequestClass.setApprovedBy(approvedBy);
                leaveRequestClass.setApprovalDate(LocalDate.now());
                leaveRequestClass.setComments(comments);
                LeaveRequestClass updatedLeaveRequest = leaveRequestRepository.save(leaveRequestClass);
                return convertToResDTO(updatedLeaveRequest);
            }
        }
        return null;
    }

    @Transactional
    public LeaveRequestResDTO rejectLeaveRequest(String tenantId, Long leaveRequestId, String approvedBy, String comments) {
        Optional<LeaveRequestClass> leaveRequest = leaveRequestRepository.findById(leaveRequestId);
        if (leaveRequest.isPresent() && leaveRequest.get().getTenantId().equals(tenantId)) {
            LeaveRequestClass leaveRequestClass = leaveRequest.get();
            if (leaveRequestClass.getStatus().equals(LeaveRequestClass.Status.PENDING)) {
                leaveRequestClass.setStatus(LeaveRequestClass.Status.REJECTED);
                leaveRequestClass.setApprovedBy(approvedBy);
                leaveRequestClass.setApprovalDate(LocalDate.now());
                leaveRequestClass.setComments(comments);
                LeaveRequestClass updatedLeaveRequest = leaveRequestRepository.save(leaveRequestClass);
                return convertToResDTO(updatedLeaveRequest);
            }
        }
        return null;
    }

    private LeaveRequestResDTO convertToResDTO(LeaveRequestClass leaveRequest) {
        LeaveRequestResDTO resDTO = new LeaveRequestResDTO();
        resDTO.setId(leaveRequest.getId());
        resDTO.setTenantId(leaveRequest.getTenantId());
        resDTO.setEmployeeId(leaveRequest.getEmployee().getId());
        resDTO.setEmployeeName(leaveRequest.getEmployee().getName());
        resDTO.setLeaveType(leaveRequest.getLeaveType().toString());
        resDTO.setStartDate(leaveRequest.getStartDate());
        resDTO.setEndDate(leaveRequest.getEndDate());
        resDTO.setNumberOfDays(leaveRequest.getNumberOfDays());
        resDTO.setReason(leaveRequest.getReason());
        resDTO.setStatus(leaveRequest.getStatus().toString());
        resDTO.setApprovedBy(leaveRequest.getApprovedBy());
        resDTO.setApprovalDate(leaveRequest.getApprovalDate());
        resDTO.setComments(leaveRequest.getComments());
        resDTO.setCreatedAt(leaveRequest.getCreatedAt());
        resDTO.setUpdatedAt(leaveRequest.getUpdatedAt());
        return resDTO;
    }

}
