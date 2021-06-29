package one.digitalinnovation.productstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductStockException extends Exception{
    public ProductStockException(Long serial, int quantityToDecrement) {
        super(String.format("Product with serial %s has a smaller stock than the amount to decrement: %s", serial, quantityToDecrement));
    }
}
