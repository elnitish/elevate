package com.elevate.insc.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "stock_levels")
public class StockLevelClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stock_level_product"))
    private ProductClass product;

    @Column(name = "quantity")
    private Integer quantity;

    public StockLevelClass(ProductClass product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

}
