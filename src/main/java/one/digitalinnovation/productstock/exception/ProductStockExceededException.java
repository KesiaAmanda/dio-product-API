package one.digitalinnovation.productstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductStockExceededException extends Exception {
    public ProductStockExceededException(Long serial, int quantityToIncrement) {
        super(String.format("Product with %s ID to increment informed exceeds the max stock capacity: %s", serial, quantityToIncrement));
    }
}
