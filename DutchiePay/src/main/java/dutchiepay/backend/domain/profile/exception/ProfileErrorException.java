package dutchiepay.backend.domain.profile.exception;

import lombok.Getter;

@Getter
public class ProfileErrorException extends RuntimeException {
    private final ProfileErrorCode profileErrorCode;

    public ProfileErrorException(ProfileErrorCode profileErrorCode) {
        super(profileErrorCode.getMessage());
        this.profileErrorCode = profileErrorCode;
    }

    @Override
    public String toString() {
        return String.format("ProfileErrorException(code=%s, message=%s)",
                profileErrorCode.name(), profileErrorCode.getMessage());
    }
}
