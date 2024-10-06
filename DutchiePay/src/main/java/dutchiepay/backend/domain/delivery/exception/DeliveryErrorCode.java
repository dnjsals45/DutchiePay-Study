package dutchiepay.backend.domain.delivery.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DeliveryErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "주소 정보가 없습니다."),
    ADDRESS_COUNT_LIMIT(HttpStatus.BAD_REQUEST, "주소는 최대 5개까지 등록 가능합니다.");

    /**
     * 401 UNAUTHORIZED
     */

    /**
     * 403 FORBIDDEN
     */

    private final HttpStatus httpStatus;
    private final String message;
}
