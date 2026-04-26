package com.elevate.pricing.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.pricing.dto.DiscountReqDTO;
import com.elevate.pricing.dto.DiscountResDTO;
import com.elevate.pricing.entity.DiscountClass;
import com.elevate.pricing.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Transactional
    public ApiResponse<?> createDiscount(String tenantId, DiscountReqDTO dto) {
        if (discountRepository.existsByTenantIdAndName(tenantId, dto.getName())) {
            return new ApiResponse<>("Discount with name '" + dto.getName() + "' already exists", 400, null);
        }

        DiscountClass d = new DiscountClass();
        d.setId(java.util.UUID.randomUUID().toString());
        d.setTenantId(tenantId);
        d.setName(dto.getName());
        d.setDiscountType(DiscountClass.DiscountType.valueOf(dto.getDiscountType().toUpperCase()));
        d.setValue(dto.getValue());
        d.setIsActive(true);

        if (dto.getAppliesTo() != null) {
            d.setAppliesTo(DiscountClass.AppliesTo.valueOf(dto.getAppliesTo().toUpperCase()));
        }
        if (dto.getMinOrderAmount() != null) d.setMinOrderAmount(dto.getMinOrderAmount());
        if (dto.getValidFrom() != null) d.setValidFrom(LocalDate.parse(dto.getValidFrom()));
        if (dto.getValidTo() != null) d.setValidTo(LocalDate.parse(dto.getValidTo()));

        DiscountClass saved = discountRepository.save(d);
        return new ApiResponse<>("Discount created successfully", 201, new DiscountResDTO(saved));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getAllDiscounts(String tenantId) {
        List<DiscountResDTO> list = discountRepository.findByTenantId(tenantId)
                .stream().map(DiscountResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Discounts retrieved successfully", 200, list);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getActiveDiscounts(String tenantId) {
        List<DiscountResDTO> list = discountRepository.findByTenantIdAndIsActiveTrue(tenantId)
                .stream().map(DiscountResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Active discounts retrieved", 200, list);
    }

    @Transactional
    public ApiResponse<?> deactivateDiscount(String tenantId, String discountId) {
        Optional<DiscountClass> opt = discountRepository.findByTenantIdAndId(tenantId, discountId);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Discount not found", 404, null);
        }
        DiscountClass d = opt.get();
        d.setIsActive(false);
        discountRepository.save(d);
        return new ApiResponse<>("Discount deactivated", 200, null);
    }
}
