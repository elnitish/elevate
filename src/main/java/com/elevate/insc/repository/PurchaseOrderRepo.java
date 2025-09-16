package com.elevate.insc.repository;

import com.elevate.insc.entity.PurchaseOrderClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrderClass, Long> {
}
