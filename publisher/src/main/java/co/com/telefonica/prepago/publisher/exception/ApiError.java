package co.com.telefonica.prepago.publisher.exception;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiError {
    private String code;
    private String localizedMessage;
    private List<ExceptionDetail> exceptionDetails;
    private String message;
    private ErrorCategory category;
    private boolean resolved;
}
