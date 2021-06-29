package one.digitalinnovation.productstock.mapper;

import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toModel(ProductDTO productDTO);

    ProductDTO toDTO(Product product);

}
