package one.digitalinnovation.productstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends Exception{
    public ProductNotFoundException(String productName) {
        super(String.format("Product with name %s not found in the system.", productName));
    }

    public ProductNotFoundException(Long serial) {
        super(String.format("Product with serial %s not found in the system.", serial));
    }
}
