package dutchiepay.backend.domain.order.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReviewErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */

    ALREADY_EXIST(HttpStatus.BAD_REQUEST, "작성한 상품 리뷰가 이미 존재합니다."),
    INVALID_REVIEW(HttpStatus.BAD_REQUEST, "존재하지 않는 리뷰입니다."),
    CANNOT_UPDATE_CAUSE_30DAYS(HttpStatus.BAD_REQUEST, "리뷰 작성 후 30일이 지나 수정할 수 없습니다."),
    CANNOT_UPDATE_CAUSE_COUNT(HttpStatus.BAD_REQUEST, "리뷰 수정은 2회까지만 가능합니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "올바르지 않은 평점입니다."),;

    private final HttpStatus httpStatus;
    private final String message;
}
