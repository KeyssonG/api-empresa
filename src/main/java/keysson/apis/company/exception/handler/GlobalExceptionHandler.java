package keysson.apis.company.exception.handler;

import keysson.apis.company.exception.BusinessRuleException;
import keysson.apis.company.exception.enums.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<SimpleErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        ErrorCode code = ex.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(new SimpleErrorResponse(
                        code.getStatus().value(),
                        code.getMessage(),
                        LocalDateTime.now()
                ));
    }
}