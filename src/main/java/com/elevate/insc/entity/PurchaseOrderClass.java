
package com.elevate.insc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_orders")
public class PurchaseOrderClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, RECEIVED, CANCELLED
    }

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PurchaseOrderItemClass> items;
}
