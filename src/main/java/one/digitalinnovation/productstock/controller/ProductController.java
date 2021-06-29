package one.digitalinnovation.productstock.controller;

import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.dto.QuantityDTO;
import one.digitalinnovation.productstock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.productstock.exception.ProductNotFoundException;
import one.digitalinnovation.productstock.exception.ProductStockExceededException;
import one.digitalinnovation.productstock.exception.ProductStockException;
import one.digitalinnovation.productstock.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController implements ProductControllerDocs{

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO) throws ProductAlreadyRegisteredException {
        return productService.createProduct(productDTO);
    }

    @GetMapping("/{name}")
    public ProductDTO findByName(@PathVariable String name) throws ProductNotFoundException {
        return productService.findByName(name);
    }

    @GetMapping
    public List<ProductDTO> listProducts() {
        return productService.listAll();
    }

    @DeleteMapping("/{serial}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(Long serial) throws ProductNotFoundException {
        productService.deleteBySerial(serial);
    }

    @PatchMapping("/{serial}/increment")
    public ProductDTO increment(@PathVariable Long serial, @RequestBody @Valid QuantityDTO quantityDTO) throws ProductNotFoundException, ProductStockExceededException {
        return productService.increment(serial, quantityDTO.getQuantity());
    }

    @PatchMapping("/{serial}/decrement")
    public ProductDTO decrement(@PathVariable Long serial, @RequestBody @Valid QuantityDTO quantityDTO) throws ProductNotFoundException, ProductStockException {
        return productService.decrement(serial, quantityDTO.getQuantity());
    }
}
