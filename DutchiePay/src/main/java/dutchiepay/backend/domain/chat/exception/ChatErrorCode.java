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
    INVALID_CHAT(HttpStatus.BAD_REQUEST, "유효하지 않은 채팅방입니다."),
    FULL_CHAT(HttpStatus.BAD_REQUEST, "채팅방이 가득 찼습니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 타입입니다."),
    OWNER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "방장은 채팅방을 나갈 수 없습니다."),
    ALREADY_JOINED(HttpStatus.BAD_REQUEST, "이미 채팅방에 참여되어있습니다."),

    /**
     * 403 Forbidden
     */
    USER_BANNED(HttpStatus.FORBIDDEN, "사용자가 채팅방에서 차단되었습니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "방장 권한이 없습니다."),;

    private final HttpStatus httpStatus;
    private final String message;
}
