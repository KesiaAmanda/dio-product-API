package one.digitalinnovation.productstock.builder;

import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.enums.ProductType;
import lombok.Builder;

@Builder
public class ProductDTOBuilder {

    @Builder.Default
    private Long serial = 0000000001L;

    @Builder.Default
    private String name = "Todo Dia";

    @Builder.Default
    private String brand = "Magnus";

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private double purchasePrice = 50.00;

    @Builder.Default
    private ProductType type = ProductType.RACAO;

    public ProductDTO toProductDTO(){
        return new ProductDTO(serial,
                name,
                brand,
                quantity,
                max,
                purchasePrice,
                type);
    }
}
