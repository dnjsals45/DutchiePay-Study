package dutchiepay.backend.global.Exception;

import org.springframework.http.HttpStatus;

public interface StatusCode {
    HttpStatus getHttpStatus();
    String getMessage();
}
