package com.elevate.fna.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elevate.fna.entity.InvoiceItemsClass;

@Repository
public interface InvoiceItemClassRepo extends JpaRepository<InvoiceItemsClass,Long > {

}
