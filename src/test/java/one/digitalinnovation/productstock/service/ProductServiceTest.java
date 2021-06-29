package one.digitalinnovation.productstock.service;

import one.digitalinnovation.productstock.builder.ProductDTOBuilder;
import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.entity.Product;
import one.digitalinnovation.productstock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.productstock.exception.ProductNotFoundException;
import one.digitalinnovation.productstock.exception.ProductStockExceededException;
import one.digitalinnovation.productstock.exception.ProductStockException;
import one.digitalinnovation.productstock.mapper.ProductMapper;
import one.digitalinnovation.productstock.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final long INVALID_PRODUCT_ID = 1L;

    @Mock
    private ProductRepository productRepository;

    private ProductMapper productMapper = ProductMapper.INSTANCE;

    @InjectMocks
    private ProductService productService;

    @Test
    void whenProductInformatedThenItShouldBeCreated() throws ProductAlreadyRegisteredException {
        //given
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedSavedProduct = productMapper.toModel(expectedProductDTO);

        //when
        when(productRepository.findByName(expectedProductDTO.getName())).thenReturn(Optional.empty());
        when(productRepository.save(expectedSavedProduct)).thenReturn(expectedSavedProduct);

        //then
        ProductDTO createdProductDTO = productService.createProduct(expectedProductDTO);

        //MatcherAssert.assertThat(createdProductDTO.getSerial(), Matchers.is(Matchers.equalTo(expectedProductDTO.getSerial())));
        assertThat(createdProductDTO.getSerial(), is(equalTo(expectedProductDTO.getSerial())));
        assertThat(createdProductDTO.getName(), is(equalTo(expectedProductDTO.getName())));
        assertThat(createdProductDTO.getQuantity(), is(equalTo(expectedProductDTO.getQuantity())));

        assertEquals(expectedProductDTO.getSerial(), createdProductDTO.getSerial());
        assertEquals(expectedProductDTO.getName(),createdProductDTO.getName());
    }

    @Test
    void whenAlreadyRegistredProductInformedThenAnExceptionShouldBeThrown(){
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product duplicatedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findByName(expectedProductDTO.getName())).thenReturn(Optional.of(duplicatedProduct));

        assertThrows(ProductAlreadyRegisteredException.class, () -> productService.createProduct(expectedProductDTO));
    }

    @Test
    void whenValidProductNameIsGivenThenReturnAProduct() throws ProductNotFoundException {
        //given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedFoundProduct = productMapper.toModel(expectedFoundProductDTO);

        //when
        when(productRepository.findByName(expectedFoundProduct.getName())).thenReturn(Optional.of(expectedFoundProduct));

        //then
        ProductDTO foundProductDTO = productService.findByName(expectedFoundProductDTO.getName());

        assertThat(foundProductDTO, is(equalTo(expectedFoundProductDTO)));

    }

    @Test
    void whenNotRegisteredProductNameIsGivenThenThrowAnException() {
        //given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();

        //when
        when(productRepository.findByName(expectedFoundProductDTO.getName())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.findByName(expectedFoundProductDTO.getName()));
    }

    @Test
    void whenListProductIsCalledThenReturnAListOfProducts() {
        //given
        ProductDTO expectedFoundProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedFoundProduct = productMapper.toModel(expectedFoundProductDTO);

        //when
        when(productRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundProduct));

        //then
        List<ProductDTO> foundListProductDTO = productService.listAll();

        assertThat(foundListProductDTO, is(not(empty())));
        assertThat(foundListProductDTO.get(0), is(equalTo(expectedFoundProductDTO)));
    }

    @Test
    void whenListproductIsCalledThenReturnAnEmptyListOfProduct() {
        //when
        when(productRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<ProductDTO> foundListProductDTO = productService.listAll();

        assertThat(foundListProductDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidSerialThenAProductShouldBeDeleted() throws ProductNotFoundException {
        //given
        ProductDTO expectedDeletedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedDeletedProduct = productMapper.toModel(expectedDeletedProductDTO);

        //when
        when(productRepository.findById(expectedDeletedProductDTO.getSerial())).thenReturn(Optional.of(expectedDeletedProduct));
        doNothing().when(productRepository).deleteById(expectedDeletedProductDTO.getSerial());

        //then
        productService.deleteBySerial(expectedDeletedProductDTO.getSerial());

        verify(productRepository, times(1)).findById(expectedDeletedProductDTO.getSerial());
        verify(productRepository, times(1)).deleteById(expectedDeletedProduct.getSerial());
    }

    @Test
    void whenIncrementIsCalledThenDecrementProductStock() throws ProductNotFoundException, ProductStockExceededException {
        //given
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        //when
        when(productRepository.findById(expectedProductDTO.getSerial())).thenReturn(Optional.of(expectedProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        int quantityToIncrement = 5;
        int expectedQuantityAfterDecrement = expectedProductDTO.getQuantity() + quantityToIncrement;

        //then
        ProductDTO incrementedProductDTO = productService.increment(expectedProductDTO.getSerial(), quantityToIncrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedProductDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, lessThan(expectedProductDTO.getMax()));
    }

    @Test
    void whenDecrementIsCalledThenDecrementProductStock() throws ProductNotFoundException, ProductStockException {
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findById(expectedProductDTO.getSerial())).thenReturn(Optional.of(expectedProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedProductDTO.getQuantity() - quantityToDecrement;
        ProductDTO incrementedProductDTO = productService.decrement(expectedProductDTO.getSerial(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedProductDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThanOrEqualTo(0));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyProductStock() throws ProductNotFoundException, ProductStockException {
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findById(expectedProductDTO.getSerial())).thenReturn(Optional.of(expectedProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedProduct.getQuantity() - quantityToDecrement;
        ProductDTO incrementedProductDTO = productService.decrement(expectedProductDTO.getSerial(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(0));
        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedProductDTO.getQuantity()));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        ProductDTO expectedProductDTO = ProductDTOBuilder.builder().build().toProductDTO();
        Product expectedProduct = productMapper.toModel(expectedProductDTO);

        when(productRepository.findById(expectedProductDTO.getSerial())).thenReturn(Optional.of(expectedProduct));

        int quantityToDecrement = 80;
        assertThrows(ProductStockException.class, () -> productService.decrement(expectedProductDTO.getSerial(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidSerialThenThrowException() {
        int quantityToDecrement = 10;

        when(productRepository.findById(INVALID_PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.decrement(INVALID_PRODUCT_ID, quantityToDecrement));
    }
}
