package com.elevate.pricing.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.crm.entity.CustomerClass;
import com.elevate.pricing.dto.*;
import com.elevate.pricing.entity.PriceListClass;
import com.elevate.pricing.entity.PriceListItemClass;
import com.elevate.pricing.repository.PriceListItemRepository;
import com.elevate.pricing.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceListService {

    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;

    @Autowired
    public PriceListService(PriceListRepository priceListRepository,
                            PriceListItemRepository priceListItemRepository) {
        this.priceListRepository = priceListRepository;
        this.priceListItemRepository = priceListItemRepository;
    }

    @Transactional
    public ApiResponse<?> createPriceList(String tenantId, PriceListReqDTO dto) {
        if (priceListRepository.existsByTenantIdAndName(tenantId, dto.getName())) {
            return new ApiResponse<>("Price list with name '" + dto.getName() + "' already exists", 400, null);
        }

        PriceListClass pl = new PriceListClass();
        pl.setId(java.util.UUID.randomUUID().toString());
        pl.setTenantId(tenantId);
        pl.setName(dto.getName());
        pl.setIsActive(true);

        if (dto.getCustomerType() != null) {
            pl.setCustomerType(CustomerClass.CustomerType.valueOf(dto.getCustomerType().toUpperCase()));
        }

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            // Unset current default
            priceListRepository.findByTenantIdAndIsDefaultTrueAndIsActiveTrue(tenantId)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        priceListRepository.save(existing);
                    });
            pl.setIsDefault(true);
        }

        if (dto.getEffectiveFrom() != null) pl.setEffectiveFrom(LocalDate.parse(dto.getEffectiveFrom()));
        if (dto.getEffectiveTo() != null) pl.setEffectiveTo(LocalDate.parse(dto.getEffectiveTo()));

        PriceListClass saved = priceListRepository.save(pl);
        return new ApiResponse<>("Price list created successfully", 201, new PriceListResDTO(saved));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getAllPriceLists(String tenantId) {
        List<PriceListResDTO> lists = priceListRepository.findByTenantId(tenantId)
                .stream().map(PriceListResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Price lists retrieved successfully", 200, lists);
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getPriceListById(String tenantId, String priceListId) {
        Optional<PriceListClass> opt = priceListRepository.findByTenantIdAndId(tenantId, priceListId);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Price list not found", 404, null);
        }
        return new ApiResponse<>("Price list retrieved", 200, new PriceListResDTO(opt.get()));
    }

    @Transactional
    public ApiResponse<?> addItem(String tenantId, String priceListId, PriceListItemReqDTO dto) {
        Optional<PriceListClass> plOpt = priceListRepository.findByTenantIdAndId(tenantId, priceListId);
        if (plOpt.isEmpty()) {
            return new ApiResponse<>("Price list not found", 404, null);
        }

        PriceListItemClass item = new PriceListItemClass();
        item.setId(java.util.UUID.randomUUID().toString());
        item.setPriceListId(priceListId);
        item.setProductId(dto.getProductId());
        item.setUnitPrice(dto.getUnitPrice());
        item.setMinQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1);
        item.setMaxQuantity(dto.getMaxQuantity());
        item.setDiscountPercent(dto.getDiscountPercent() != null ? dto.getDiscountPercent() : BigDecimal.ZERO);

        PriceListItemClass saved = priceListItemRepository.save(item);
        return new ApiResponse<>("Price list item added successfully", 201, new PriceListItemResDTO(saved));
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getItems(String tenantId, String priceListId) {
        Optional<PriceListClass> plOpt = priceListRepository.findByTenantIdAndId(tenantId, priceListId);
        if (plOpt.isEmpty()) {
            return new ApiResponse<>("Price list not found", 404, null);
        }

        List<PriceListItemResDTO> items = priceListItemRepository.findByPriceListId(priceListId)
                .stream().map(PriceListItemResDTO::new).collect(Collectors.toList());
        return new ApiResponse<>("Price list items retrieved", 200, items);
    }

    @Transactional
    public ApiResponse<?> deleteItem(String tenantId, String priceListId, String itemId) {
        Optional<PriceListClass> plOpt = priceListRepository.findByTenantIdAndId(tenantId, priceListId);
        if (plOpt.isEmpty()) {
            return new ApiResponse<>("Price list not found", 404, null);
        }

        priceListItemRepository.deleteById(itemId);
        return new ApiResponse<>("Price list item deleted", 200, null);
    }

    @Transactional
    public ApiResponse<?> deactivatePriceList(String tenantId, String priceListId) {
        Optional<PriceListClass> opt = priceListRepository.findByTenantIdAndId(tenantId, priceListId);
        if (opt.isEmpty()) {
            return new ApiResponse<>("Price list not found", 404, null);
        }
        PriceListClass pl = opt.get();
        pl.setIsActive(false);
        priceListRepository.save(pl);
        return new ApiResponse<>("Price list deactivated", 200, null);
    }
}
