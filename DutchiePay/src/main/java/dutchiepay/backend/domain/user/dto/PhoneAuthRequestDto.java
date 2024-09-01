package dutchiepay.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneAuthRequestDto {
    @NotBlank
    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11자리 숫자여야 합니다.")
    private String phone;
}
