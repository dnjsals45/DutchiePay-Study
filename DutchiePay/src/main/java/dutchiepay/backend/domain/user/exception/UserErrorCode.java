package dutchiepay.backend.domain.user.exception;

import dutchiepay.backend.global.Exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 유저가 없습니다.");

    /**
     * 401 UNAUTHORIZED
     */


    /**
     * 403 FORBIDDEN
     */

    private final HttpStatus httpStatus;
    private final String message;
}
