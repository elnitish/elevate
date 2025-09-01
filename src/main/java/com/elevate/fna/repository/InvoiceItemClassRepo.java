package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceItemsClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemClassRepo extends JpaRepository<InvoiceItemsClass,Long> {

}
