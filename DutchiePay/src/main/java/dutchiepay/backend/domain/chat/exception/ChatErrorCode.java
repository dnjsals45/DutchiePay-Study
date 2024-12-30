package dutchiepay.backend.domain.chat.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChatErrorCode implements StatusCode {

    /**
     * 400 Bad Request
     */
    INVALID_CHAT(HttpStatus.BAD_REQUEST, "유효하지 않은 채팅방입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
