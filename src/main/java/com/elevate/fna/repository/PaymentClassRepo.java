package com.elevate.fna.repository;

import com.elevate.fna.entity.PaymentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentClassRepo extends JpaRepository<PaymentClass, Long> {

}
