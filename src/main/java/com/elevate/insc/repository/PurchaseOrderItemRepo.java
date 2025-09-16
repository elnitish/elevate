package com.elevate.insc.repository;

import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.PurchaseOrderItemClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderItemRepo extends JpaRepository<PurchaseOrderItemClass, Integer> {


    Optional<ProductClass> findById(Long productId);
}
