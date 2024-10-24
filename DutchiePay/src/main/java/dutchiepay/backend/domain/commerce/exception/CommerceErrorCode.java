package dutchiepay.backend.domain.commerce.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommerceErrorCode implements StatusCode {
    /**
     * 400 Bad Request
     */
    AFTER_DUE_DATE(HttpStatus.BAD_REQUEST, "마감된 상품입니다."),
    INVALID_FILTER(HttpStatus.BAD_REQUEST, "유효하지 않은 필터입니다."),

    /**
     * 404 Not Found
     */
    CANNOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
