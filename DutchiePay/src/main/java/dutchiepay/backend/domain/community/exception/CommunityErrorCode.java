package dutchiepay.backend.domain.community.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommunityErrorCode implements StatusCode {
    /**
     * 400 Bad Request
     */
    INVALID_SHARE(HttpStatus.BAD_REQUEST, "유효하지 않은 게시글입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다."),;


    private final HttpStatus httpStatus;
    private final String message;
}
