package dutchiepay.backend.global.payment.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentErrorCode {

    /**
     * 400 BAD_REQUEST
     */
    INVALID_PRODUCT(HttpStatus.BAD_REQUEST, "상품 정보가 없습니다."),
    INVALID_BUY(HttpStatus.BAD_REQUEST, "구매 정보가 없습니다."),
    FINISHED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 처리된 요청입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "정보가 유효하지 않습니다."),

    /**
     * 500 Internal_Server_Error
     */
    PORTONE_CANCEL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
