package dutchiepay.backend.domain.notice.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NoticeErrorCode implements StatusCode {
    INVALID_NOTICE(HttpStatus.BAD_REQUEST, "유효하지 않은 알림입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
