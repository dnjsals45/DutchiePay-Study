package dutchiepay.backend.global.exception;

import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "CustomExceptionHandler")
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserErrorException.class)
    protected ResponseEntity<?> handleUserErrorException(UserErrorException e) {
        log.warn("handleUserErrorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getUserErrorCode().getHttpStatus()).body(message);
    }

    @ExceptionHandler(ProfileErrorException.class)
    protected ResponseEntity<?> handleProcessorException(ProfileErrorException e) {
        log.warn("handleProcessorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getProfileErrorCode().getHttpStatus()).body(message);
    }
}
