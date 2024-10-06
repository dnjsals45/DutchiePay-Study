package dutchiepay.backend.domain.user.exception;

import dutchiepay.backend.global.exception.StatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements StatusCode {
    /**
     * 400 BAD_REQUEST
     */
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 유저가 없습니다."),
    USER_NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    USER_PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 전화번호입니다."),
    USER_NICKNAME_MISSING(HttpStatus.BAD_REQUEST, "닉네임을 입력해 주세요."),
    USER_EMAIL_MISSING(HttpStatus.BAD_REQUEST, "이메일을 입력해 주세요."),
    USER_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다."),
    USER_EMAIL_SUSPENDED(HttpStatus.BAD_REQUEST, "정지된 회원의 이메일은 사용할 수 없습니다."),
    USER_EMAIL_TERMINATED(HttpStatus.BAD_REQUEST, "탈퇴한 회원의 이메일입니다. 탈퇴 후 재가입 할 수 없습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    DELETED_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 회원입니다."),
    USER_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 새로운 비밀번호가 같습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호가 올바르지 않습니다."),

    /**
     * 401 UNAUTHORIZED
     */
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 유효하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "정지된 회원입니다."),
    USER_TERMINATED(HttpStatus.FORBIDDEN, "탈퇴한 회원입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
