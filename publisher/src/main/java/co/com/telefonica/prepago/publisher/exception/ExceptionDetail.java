package co.com.telefonica.prepago.publisher.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ExceptionDetail {
    private String code;
    private String component;
    private String message;
    private List<String> arguments;
    private String endpoint;
}
