package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceClassRepo extends JpaRepository<InvoiceClass, Integer> {
    Optional<InvoiceClass> findById(Long id);
    List<InvoiceClass> findByStatus(String status);

}
