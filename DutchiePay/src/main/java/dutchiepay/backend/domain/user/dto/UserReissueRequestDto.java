package dutchiepay.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReissueRequestDto {

    @NotBlank(message = "액세스 토큰을 입력해주세요.")
    private String access;

    @NotBlank(message = "리프레시 토큰을 입력해주세요.")
    private String refresh;
}
