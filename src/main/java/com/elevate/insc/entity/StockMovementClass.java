package com.elevate.insc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "stock_movements")
public class StockMovementClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private Integer quantity;
    private LocalDateTime date;
    private String reference;

    public enum MovementType {
        IN, OUT
    }
    public StockMovementClass(Long productId, MovementType type, Integer quantity, LocalDateTime date,String reference) {
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
        this.date = date;
        this.reference = reference;
    }
}
