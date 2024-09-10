package dutchiepay.backend.domain.profile.exception;

import dutchiepay.backend.global.Exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProfileErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    INVALID_ASK(HttpStatus.BAD_REQUEST, "문의 정보가 없습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    DELETE_USER_MISSMATCH(HttpStatus.UNAUTHORIZED, "본인만 문의 삭제를 할 수 있습니다.");

    /**
     * 403 FORBIDDEN
     */

    private final HttpStatus httpStatus;
    private final String message;
}
