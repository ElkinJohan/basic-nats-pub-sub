package co.com.telefonica.prepago.publisher.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.com.telefonica.prepago.publisher.util.Const.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${controller.properties.base-path}")
    private String basePath;

    @Value("${spring.application.name}")
    private String name;
    private final Map<String, Object> errors = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiError> handleApiException(Exception exception) {
        ApiError apiError = buildException(exception);
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(apiError.getCode()));
        return new ResponseEntity<>(apiError, httpStatus);
    }

    private ApiError buildException(Exception exception) {
        List<ExceptionDetail> exceptionDetailList = new ArrayList<>();
        if (exception instanceof ApiException apiException) {
            Map<String, Object> errorInformation = (Map<String, Object>) errors.get(apiException.getSpecificError());
            exceptionDetailList.add(ExceptionDetail.builder()
                    .code((String) errorInformation.get("code"))
                    .component(name)
                    .message(errorInformation.get("message") + ": " + apiException.getMessage())
                    .arguments(new ArrayList<>())
                    .endpoint(basePath + "/" + name)
                    .build());
            LOGGER.info("Throwing exception: {} {}", errorInformation.get("message"), errorInformation.get("code"));

            return ApiError.builder()
                    .localizedMessage((String) errorInformation.get("http-description"))
                    .category(ErrorCategory.valueOf(((String) errorInformation.get("category")).toUpperCase().replace(" ", "_")))
                    .code((String) errorInformation.get("http-code"))
                    .message((String) errorInformation.get("http-description"))
                    .exceptionDetails(exceptionDetailList)
                    .resolved(true)
                    .build();
        }

        LOGGER.info("Throwing exception: {} . Please verify the error name", exception.getMessage());
        exceptionDetailList.add(ExceptionDetail.builder()
                .code(GENERAL_ERROR_CODE)
                .component(name)
                .message(exception.getMessage())
                .arguments(new ArrayList<>())
                .build());

        return ApiError.builder()
                .localizedMessage(INFORMATION_NOT_FOUND)
                .category(ErrorCategory.INVALID_CONFIGURATION)
                .code(INTERNAL_SERVER_ERROR)
                .message(INFORMATION_NOT_FOUND)
                .exceptionDetails(exceptionDetailList)
                .resolved(true)
                .build();
    }
}

