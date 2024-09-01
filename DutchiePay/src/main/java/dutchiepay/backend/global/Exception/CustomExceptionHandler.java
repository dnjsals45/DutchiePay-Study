package dutchiepay.backend.global.Exception;

import dutchiepay.backend.domain.user.exception.UserErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserErrorException.class)
    protected ResponseEntity<?> handleUserErrorException(UserErrorException e) {
        log.warn("handleUserErrorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getUserErrorCode().getHttpStatus()).body(message);
    }
}
