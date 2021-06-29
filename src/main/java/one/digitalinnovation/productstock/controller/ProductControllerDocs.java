package one.digitalinnovation.productstock.controller;

import one.digitalinnovation.productstock.dto.ProductDTO;
import one.digitalinnovation.productstock.exception.ProductAlreadyRegisteredException;
import one.digitalinnovation.productstock.exception.ProductNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages product stock")
public interface ProductControllerDocs {
    @ApiOperation(value = "Product creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success product creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    ProductDTO createProduct(ProductDTO productDTO) throws ProductAlreadyRegisteredException;

    @ApiOperation(value = "Returns product found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success product found in the system"),
            @ApiResponse(code = 404, message = "Product with given name not found.")
    })
    ProductDTO findByName(@PathVariable String name) throws ProductNotFoundException;

    @ApiOperation(value = "Returns a list of all product registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all product registered in the system"),
    })
    List<ProductDTO> listProducts();

    @ApiOperation(value = "Delete a product found by a given valid serial")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success product deleted in the system"),
            @ApiResponse(code = 404, message = "Product with given serial not found.")
    })
    void deleteById(@PathVariable Long serial) throws ProductNotFoundException;
}
