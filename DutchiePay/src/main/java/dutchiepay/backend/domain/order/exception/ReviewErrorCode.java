package dutchiepay.backend.domain.order.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReviewErrorCode {
    /**
     * 400 BAD_REQUEST
     */

    ALREADY_EXIST(HttpStatus.BAD_REQUEST, "작성한 상품 리뷰가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
