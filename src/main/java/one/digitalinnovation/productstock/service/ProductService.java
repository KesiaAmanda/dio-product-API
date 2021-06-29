package one.digitalinnovation.productstock.service;

import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.entity.Product;
import one.digitalinnovation.productstock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.productstock.exception.ProductNotFoundException;
import one.digitalinnovation.productstock.exception.ProductStockExceededException;
import one.digitalinnovation.productstock.exception.ProductStockException;
import one.digitalinnovation.productstock.mapper.ProductMapper;
import one.digitalinnovation.productstock.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductDTO createProduct(ProductDTO productDTO) throws ProductAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(productDTO.getName());
        Product product = productMapper.toModel(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    public ProductDTO findByName(String name) throws ProductNotFoundException {
        Product foundProduct = productRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException(name));
        return productMapper.toDTO(foundProduct);
    }

    public ProductDTO findById(Long serial) throws ProductNotFoundException {
        Product foundProduct = productRepository.findById(serial)
                .orElseThrow(() -> new ProductNotFoundException(serial));
        return productMapper.toDTO(foundProduct);
    }

    public List<ProductDTO> listAll(){
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteBySerial(Long serial) throws ProductNotFoundException {
        verifyIfExists(serial);
        productRepository.deleteById(serial);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws ProductAlreadyRegisteredException {
        Optional<Product> optSavedProduct = productRepository.findByName(name);
        if (optSavedProduct.isPresent()) {
            throw new ProductAlreadyRegisteredException(name);
        }
    }

    private Product verifyIfExists(Long serial) throws ProductNotFoundException {
        return productRepository.findById(serial)
                .orElseThrow(() -> new ProductNotFoundException(serial));
    }

    public ProductDTO increment(Long serial, int quantityToIncrement) throws ProductNotFoundException, ProductStockExceededException {
        Product productToIncrementStock = verifyIfExists(serial);
        int quantityAfterIncrement = quantityToIncrement + productToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= productToIncrementStock.getMax()) {
            productToIncrementStock.setQuantity(productToIncrementStock.getQuantity() + quantityToIncrement);
            Product incrementedProductStock = productRepository.save(productToIncrementStock);
            return productMapper.toDTO(incrementedProductStock);
        }
        throw new ProductStockExceededException(serial, quantityToIncrement);
    }

    public ProductDTO decrement(Long serial, int quantityToDecrement) throws ProductNotFoundException, ProductStockException {
        Product productToDecrementStock = verifyIfExists(serial);
        if (quantityToDecrement <= productToDecrementStock.getQuantity()) {
            productToDecrementStock.setQuantity(productToDecrementStock.getQuantity() - quantityToDecrement);
            Product decrementedProductStock = productRepository.save(productToDecrementStock);
            return productMapper.toDTO(decrementedProductStock);
        }
        throw new ProductStockException(serial, quantityToDecrement);
    }
}
