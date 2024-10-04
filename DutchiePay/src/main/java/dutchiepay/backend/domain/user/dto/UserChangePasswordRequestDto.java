package dutchiepay.backend.domain.user.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChangePasswordRequestDto {
    private String password;
    private String newPassword;
}
