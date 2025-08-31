package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceClassRepo extends JpaRepository<InvoiceClass, Integer> {
    InvoiceClass findById(Long id);

}
