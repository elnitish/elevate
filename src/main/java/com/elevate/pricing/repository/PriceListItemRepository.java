package com.elevate.pricing.repository;

import com.elevate.pricing.entity.PriceListItemClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceListItemClass, String> {

    List<PriceListItemClass> findByPriceListId(String priceListId);

    @Query("SELECT pli FROM PriceListItemClass pli WHERE pli.priceListId = :priceListId AND pli.productId = :productId " +
           "AND pli.minQuantity <= :quantity AND (pli.maxQuantity IS NULL OR pli.maxQuantity >= :quantity) " +
           "ORDER BY pli.minQuantity DESC")
    List<PriceListItemClass> findMatchingItems(@Param("priceListId") String priceListId,
                                                @Param("productId") String productId,
                                                @Param("quantity") Integer quantity);

    List<PriceListItemClass> findByPriceListIdAndProductId(String priceListId, String productId);
}
