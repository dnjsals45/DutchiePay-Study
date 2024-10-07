package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePhoneRequestDto {
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{1,11}$", message = "전화번호는 1자리 이상 11자리 이하 숫자여야 합니다.")
    private String phone;
}
