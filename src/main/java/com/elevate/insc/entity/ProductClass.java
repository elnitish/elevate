package com.elevate.insc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "products")
public class ProductClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String productName;

    @Column(name = "description")
    private String productDescription;

    @Min(1)
    @Column(name = "price")
    private BigDecimal productPrice;


    public ProductClass(){

    }

    public ProductClass(String productName, String productDescription, BigDecimal productPrice, LocalDateTime createdDate) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }
}
