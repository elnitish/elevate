package com.elevate.pricing.service;

import com.elevate.crm.entity.CustomerClass;
import com.elevate.crm.repository.CustomerRepository;
import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.repository.ProductClassRepo;
import com.elevate.pricing.dto.ResolvedPriceDTO;
import com.elevate.pricing.entity.PriceListClass;
import com.elevate.pricing.entity.PriceListItemClass;
import com.elevate.pricing.repository.PriceListItemRepository;
import com.elevate.pricing.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PricingService {

    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductClassRepo productClassRepo;

    @Autowired
    public PricingService(PriceListRepository priceListRepository,
                          PriceListItemRepository priceListItemRepository,
                          CustomerRepository customerRepository,
                          ProductClassRepo productClassRepo) {
        this.priceListRepository = priceListRepository;
        this.priceListItemRepository = priceListItemRepository;
        this.customerRepository = customerRepository;
        this.productClassRepo = productClassRepo;
    }

    /**
     * Resolve the effective price for a product given a customer and quantity.
     *
     * Resolution priority:
     * 1. Customer-type based price list with quantity tier match
     * 2. Default price list with quantity tier match
     * 3. Product's selling price (fallback)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "priceResolution", key = "#tenantId + ':' + #productId + ':' + #customerId + ':' + #quantity")
    public ResolvedPriceDTO resolvePrice(String tenantId, String productId, Long customerId, Integer quantity) {
        Optional<ProductClass> productOpt = productClassRepo.findByTenantIdAndId(tenantId, productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        ProductClass product = productOpt.get();
        BigDecimal basePrice = product.getSellingPrice();

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        // Get customer type if customer ID provided
        CustomerClass.CustomerType customerType = null;
        if (customerId != null) {
            Optional<CustomerClass> customerOpt = customerRepository.findByTenantIdAndId(tenantId, customerId);
            if (customerOpt.isPresent()) {
                customerType = customerOpt.get().getCustomerType();
            }
        }

        LocalDate today = LocalDate.now();

        // 1. Try customer-type based price lists
        if (customerType != null) {
            List<PriceListClass> typeLists = priceListRepository
                    .findByTenantIdAndCustomerTypeAndIsActiveTrue(tenantId, customerType);

            for (PriceListClass pl : typeLists) {
                if (!isEffective(pl, today)) continue;

                List<PriceListItemClass> items = priceListItemRepository
                        .findMatchingItems(pl.getId(), productId, quantity);

                if (!items.isEmpty()) {
                    PriceListItemClass item = items.get(0); // best match (highest minQuantity)
                    BigDecimal resolvedPrice = applyDiscount(item.getUnitPrice(), item.getDiscountPercent());
                    return new ResolvedPriceDTO(
                            productId, product.getName(), basePrice, resolvedPrice,
                            item.getDiscountPercent(), pl.getName(), "CUSTOMER_TYPE");
                }
            }
        }

        // 2. Try default price list
        Optional<PriceListClass> defaultList = priceListRepository
                .findByTenantIdAndIsDefaultTrueAndIsActiveTrue(tenantId);

        if (defaultList.isPresent() && isEffective(defaultList.get(), today)) {
            List<PriceListItemClass> items = priceListItemRepository
                    .findMatchingItems(defaultList.get().getId(), productId, quantity);

            if (!items.isEmpty()) {
                PriceListItemClass item = items.get(0);
                BigDecimal resolvedPrice = applyDiscount(item.getUnitPrice(), item.getDiscountPercent());
                return new ResolvedPriceDTO(
                        productId, product.getName(), basePrice, resolvedPrice,
                        item.getDiscountPercent(), defaultList.get().getName(), "PRICE_LIST");
            }
        }

        // 3. Fallback to product selling price
        return new ResolvedPriceDTO(
                productId, product.getName(), basePrice, basePrice,
                BigDecimal.ZERO, null, "PRODUCT_DEFAULT");
    }

    /**
     * Resolve price and return just the BigDecimal value (for use in InvoiceService).
     */
    @Transactional(readOnly = true)
    public BigDecimal resolvePriceValue(String tenantId, String productId, Long customerId, Integer quantity) {
        return resolvePrice(tenantId, productId, customerId, quantity).getResolvedPrice();
    }

    private boolean isEffective(PriceListClass pl, LocalDate today) {
        if (pl.getEffectiveFrom() != null && today.isBefore(pl.getEffectiveFrom())) return false;
        if (pl.getEffectiveTo() != null && today.isAfter(pl.getEffectiveTo())) return false;
        return true;
    }

    private BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        BigDecimal discount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discount);
    }
}
