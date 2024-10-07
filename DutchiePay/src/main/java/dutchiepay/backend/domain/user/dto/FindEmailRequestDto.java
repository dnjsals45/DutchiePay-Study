package dutchiepay.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "이메일 찾기 요청 DTO")
public class FindEmailRequestDto {
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{1,11}$", message = "전화번호는 1자리 이상 11자리 이하 숫자여야 합니다.")
    private String phone;
}
