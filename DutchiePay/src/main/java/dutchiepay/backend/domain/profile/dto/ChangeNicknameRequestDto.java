package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeNicknameRequestDto {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(regexp = "^.{1,8}$", message = "닉네임은 8글자까지 가능합니다.")
    private String nickname;
}
