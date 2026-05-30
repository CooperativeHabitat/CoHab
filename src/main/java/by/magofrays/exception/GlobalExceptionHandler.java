package by.magofrays.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                e.getErrorCode(),
                e.getMessage()
        );
        problemDetail.setTitle("BusinessException");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Некорректно введенные данные."
        );

        problemDetail.setTitle("BindException");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        List<Map<String, Object>> filteredFieldErrors = ex.getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "defaultMessage", Objects.requireNonNull(error.getDefaultMessage()),
                        "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue() : ""
                ))
                .collect(Collectors.toList());

        problemDetail.setProperty("fieldErrors", filteredFieldErrors);
        return problemDetail;
    }
}
