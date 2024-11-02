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
public class UserReLoginRequestDto {

    @NotBlank(message = "리프레쉬 토큰을 입력해주세요.")
    private String refresh;
}
