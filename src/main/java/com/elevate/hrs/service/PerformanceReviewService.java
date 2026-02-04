package com.elevate.hrs.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elevate.hrs.dto.PerformanceReviewReqDTO;
import com.elevate.hrs.dto.PerformanceReviewResDTO;
import com.elevate.hrs.entity.EmployeeClass;
import com.elevate.hrs.entity.PerformanceReviewClass;
import com.elevate.hrs.repository.EmployeeClassRepo;
import com.elevate.hrs.repository.PerformanceReviewRepository;

@Service
public class PerformanceReviewService {

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    @Autowired
    private EmployeeClassRepo employeeRepository;

    @Transactional
    public PerformanceReviewResDTO createPerformanceReview(String tenantId, PerformanceReviewReqDTO performanceReviewReqDTO) {
        // Validate employee exists
        Optional<EmployeeClass> employee = employeeRepository.findById(performanceReviewReqDTO.getEmployeeId());
        if (employee.isEmpty()) {
            throw new RuntimeException("Employee not found with id: " + performanceReviewReqDTO.getEmployeeId());
        }

        PerformanceReviewClass review = new PerformanceReviewClass();
        review.setTenantId(tenantId);
        review.setEmployee(employee.get());
        review.setReviewerName(performanceReviewReqDTO.getReviewerName());
        review.setReviewDate(performanceReviewReqDTO.getReviewDate() != null ? performanceReviewReqDTO.getReviewDate() : LocalDate.now());
        
        if (performanceReviewReqDTO.getRating() != null) {
            try {
                review.setRating(PerformanceReviewClass.Rating.valueOf(performanceReviewReqDTO.getRating().toUpperCase()));
            } catch (IllegalArgumentException e) {
                review.setRating(PerformanceReviewClass.Rating.AVERAGE);
            }
        }

        review.setWorkQuality(performanceReviewReqDTO.getWorkQuality());
        review.setCommunication(performanceReviewReqDTO.getCommunication());
        review.setTeamwork(performanceReviewReqDTO.getTeamwork());
        review.setPunctuality(performanceReviewReqDTO.getPunctuality());
        review.setOverallComments(performanceReviewReqDTO.getOverallComments());
        review.setImprovementAreas(performanceReviewReqDTO.getImprovementAreas());
        review.setStrengths(performanceReviewReqDTO.getStrengths());

        PerformanceReviewClass savedReview = performanceReviewRepository.save(review);
        return convertToResDTO(savedReview);
    }

    public List<PerformanceReviewResDTO> getAllPerformanceReviews(String tenantId) {
        return performanceReviewRepository.findByTenantId(tenantId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<PerformanceReviewResDTO> getPerformanceReviewsByEmployee(String tenantId, Long employeeId) {
        return performanceReviewRepository.findByTenantIdAndEmployeeId(tenantId, employeeId).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public List<PerformanceReviewResDTO> getPerformanceReviewsByDateRange(String tenantId, LocalDate startDate, LocalDate endDate) {
        return performanceReviewRepository.findByTenantIdAndReviewDateBetween(tenantId, startDate, endDate).stream()
                .map(this::convertToResDTO)
                .collect(Collectors.toList());
    }

    public Optional<PerformanceReviewResDTO> getPerformanceReviewById(String tenantId, Long reviewId) {
        Optional<PerformanceReviewClass> review = performanceReviewRepository.findById(reviewId);
        if (review.isPresent() && review.get().getTenantId().equals(tenantId)) {
            return review.map(this::convertToResDTO);
        }
        return Optional.empty();
    }

    @Transactional
    public PerformanceReviewResDTO updatePerformanceReview(String tenantId, Long reviewId, PerformanceReviewReqDTO performanceReviewReqDTO) {
        Optional<PerformanceReviewClass> review = performanceReviewRepository.findById(reviewId);
        if (review.isPresent() && review.get().getTenantId().equals(tenantId)) {
            PerformanceReviewClass reviewClass = review.get();
            reviewClass.setReviewerName(performanceReviewReqDTO.getReviewerName());
            reviewClass.setReviewDate(performanceReviewReqDTO.getReviewDate() != null ? performanceReviewReqDTO.getReviewDate() : LocalDate.now());
            
            if (performanceReviewReqDTO.getRating() != null) {
                try {
                    reviewClass.setRating(PerformanceReviewClass.Rating.valueOf(performanceReviewReqDTO.getRating().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Keep existing rating
                }
            }

            reviewClass.setWorkQuality(performanceReviewReqDTO.getWorkQuality());
            reviewClass.setCommunication(performanceReviewReqDTO.getCommunication());
            reviewClass.setTeamwork(performanceReviewReqDTO.getTeamwork());
            reviewClass.setPunctuality(performanceReviewReqDTO.getPunctuality());
            reviewClass.setOverallComments(performanceReviewReqDTO.getOverallComments());
            reviewClass.setImprovementAreas(performanceReviewReqDTO.getImprovementAreas());
            reviewClass.setStrengths(performanceReviewReqDTO.getStrengths());

            PerformanceReviewClass updatedReview = performanceReviewRepository.save(reviewClass);
            return convertToResDTO(updatedReview);
        }
        return null;
    }

    @Transactional
    public boolean deletePerformanceReview(String tenantId, Long reviewId) {
        Optional<PerformanceReviewClass> review = performanceReviewRepository.findById(reviewId);
        if (review.isPresent() && review.get().getTenantId().equals(tenantId)) {
            performanceReviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }

    private PerformanceReviewResDTO convertToResDTO(PerformanceReviewClass review) {
        PerformanceReviewResDTO resDTO = new PerformanceReviewResDTO();
        resDTO.setId(review.getId());
        resDTO.setTenantId(review.getTenantId());
        resDTO.setEmployeeId(review.getEmployee().getId());
        resDTO.setEmployeeName(review.getEmployee().getName());
        resDTO.setReviewerName(review.getReviewerName());
        resDTO.setReviewDate(review.getReviewDate());
        resDTO.setRating(review.getRating() != null ? review.getRating().toString() : null);
        resDTO.setWorkQuality(review.getWorkQuality());
        resDTO.setCommunication(review.getCommunication());
        resDTO.setTeamwork(review.getTeamwork());
        resDTO.setPunctuality(review.getPunctuality());
        resDTO.setOverallComments(review.getOverallComments());
        resDTO.setImprovementAreas(review.getImprovementAreas());
        resDTO.setStrengths(review.getStrengths());
        resDTO.setCreatedAt(review.getCreatedAt());
        resDTO.setUpdatedAt(review.getUpdatedAt());
        return resDTO;
    }

}
