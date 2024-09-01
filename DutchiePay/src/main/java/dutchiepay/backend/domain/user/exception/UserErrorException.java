package dutchiepay.backend.domain.user.exception;

import lombok.Getter;

@Getter
public class UserErrorException extends RuntimeException {
    private final UserErrorCode userErrorCode;

    public UserErrorException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }

    @Override
    public String toString() {
        return String.format("UserErrorException(code=%s, message=%s)",
                userErrorCode.name(), userErrorCode.getMessage());
    }
}
