package one.digitalinnovation.productstock.entity;

import one.digitalinnovation.productstock.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long serial;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int max;

    @Column(nullable = false)
    private double purchasePrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType type;
}
