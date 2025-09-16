package com.elevate.insc.repository;

import com.elevate.insc.entity.StockMovementClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepo extends JpaRepository<StockMovementClass,Long> {
    List<StockMovementClass> findByProductId(Long productId);
}
