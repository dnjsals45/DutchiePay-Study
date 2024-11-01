package dutchiepay.backend.global.exception;

import dutchiepay.backend.domain.commerce.exception.CommerceException;
import dutchiepay.backend.domain.delivery.exception.DeliveryErrorException;
import dutchiepay.backend.domain.order.exception.OrderErrorException;
import dutchiepay.backend.domain.order.exception.ReviewErrorException;
import dutchiepay.backend.domain.profile.exception.ProfileErrorException;
import dutchiepay.backend.domain.user.exception.UserErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author     dnjsals45
 * @version    1.0.0
 * @since      1.0.0
 * @return     ResponseEntity<?>
 */
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

    @ExceptionHandler(DeliveryErrorException.class)
    protected ResponseEntity<?> handleDeliveryErrorException(DeliveryErrorException e) {
        log.warn("handleDeliveryErrorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getDeliveryErrorCode().getHttpStatus()).body(message);
    }

    @ExceptionHandler(OrderErrorException.class)
    protected ResponseEntity<?> handleOrdersErrorException(OrderErrorException e) {
        log.warn("handleOrdersErrorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getOrderErrorCode().getHttpStatus()).body(message);
    }

    @ExceptionHandler(CommerceException.class)
    protected ResponseEntity<?> handleCommerceException(CommerceException e) {
        log.warn("handleCommerceException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getCommerceErrorCode().getHttpStatus()).body(message);
    }

    @ExceptionHandler(ReviewErrorException.class)
    protected ResponseEntity<?> handleReviewErrorException(ReviewErrorException e) {
        log.warn("handleReviewErrorException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.status(e.getReviewErrorCode().getHttpStatus()).body(message);
    }

    /**
     * Validation 에러 처리가 된 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        log.warn("handleMethodArgumentNotValidException : {}", defaultMessage);

        final ErrorMessage message = ErrorMessage.of(defaultMessage);

        return ResponseEntity.badRequest().body(message);
    }


    /**
     * NullPointerException 에러 처리가 된 경우
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        log.warn("handleNullPointerException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("handleHttpMessageNotReadableException : {}", e.getMessage());
        final ErrorMessage message = ErrorMessage.of(e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }
}
