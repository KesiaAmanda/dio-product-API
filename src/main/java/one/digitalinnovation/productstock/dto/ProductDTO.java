package one.digitalinnovation.productstock.dto;

import one.digitalinnovation.productstock.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @NotNull
    private Long serial;

    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @NotNull
    @Size(min = 1, max = 200)
    private String brand;

    @NotNull
    @Max(100)
    private int quantity;

    @NotNull
    @Max(500)
    private int max;

    @NotNull
    @Min(0)
    private double purchasePrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductType type;
}
