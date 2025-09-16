package com.elevate.fna.repository;

import com.elevate.fna.entity.InvoiceClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceClassRepo extends JpaRepository<InvoiceClass, Long> {

    List<InvoiceClass> findByStatus(String status);
    List<InvoiceClass> findByDate(Date date);


}
