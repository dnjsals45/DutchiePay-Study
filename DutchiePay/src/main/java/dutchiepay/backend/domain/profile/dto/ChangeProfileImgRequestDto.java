package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeProfileImgRequestDto {
    @NotBlank(message = "프로필 이미지를 입력해주세요.")
    private String profileImg;
}
