package dutchiepay.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChangePasswordRequestDto {
    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$", message = "영문, 숫자, 특수문자를 모두 포함하여 8글자 이상으로 입력해주세요.")
    private String password;
    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$", message = "영문, 숫자, 특수문자를 모두 포함하여 8글자 이상으로 입력해주세요.")
    private String newPassword;
}
