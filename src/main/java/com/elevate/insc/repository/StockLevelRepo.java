package com.elevate.insc.repository;

import com.elevate.insc.entity.ProductClass;
import com.elevate.insc.entity.StockLevelClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockLevelRepo extends JpaRepository<StockLevelClass,Long> {

    Optional<StockLevelClass> findByProductId(ProductClass productId);


    Optional<StockLevelClass> findByProduct(ProductClass product);
}
