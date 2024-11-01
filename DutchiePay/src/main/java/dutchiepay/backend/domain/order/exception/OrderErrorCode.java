package dutchiepay.backend.domain.order.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "주문정보를 찾을 수 없습니다."),
    INVALID_EXCHANGE_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않은 교환 요청 타입입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    ORDER_USER_MISS_MATCH(HttpStatus.UNAUTHORIZED, "주문자 정보가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
