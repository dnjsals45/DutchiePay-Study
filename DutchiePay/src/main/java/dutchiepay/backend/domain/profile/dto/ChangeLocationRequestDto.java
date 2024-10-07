package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLocationRequestDto {
    @NotBlank(message = "지역을 입력해주세요.")
    private String location;
}
