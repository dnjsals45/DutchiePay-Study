package dutchiepay.backend.domain.order.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AskErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    INVALID_BUY(HttpStatus.BAD_REQUEST, "공동구매 정보가 없습니다."),
    INVALID_ASK(HttpStatus.BAD_REQUEST, "문의 정보가 없습니다."),;

    private final HttpStatus httpStatus;
    private final String message;
}
