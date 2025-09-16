package com.elevate.insc.repository;

import com.elevate.insc.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductClassRepo extends JpaRepository<ProductClass, Long> {
    ProductClass findById(long id);


}
