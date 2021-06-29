package one.digitalinnovation.productstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductAlreadyRegisteredException extends Exception{
    public ProductAlreadyRegisteredException(String productName) {
        super(String.format("Product with name %s already registered in the system.", productName));
    }
}
