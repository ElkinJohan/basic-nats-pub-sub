package co.com.telefonica.prepago.publisher.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends Exception {

    private final String specificError;

    public ApiException(String specificErrorString, String message) {
        super(message);
        this.specificError = specificErrorString;
    }
}
