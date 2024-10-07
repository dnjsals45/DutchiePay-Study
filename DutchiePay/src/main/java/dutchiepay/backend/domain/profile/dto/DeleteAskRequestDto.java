package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteAskRequestDto {
    @NotBlank(message = "후기 ID를 입력해주세요.")
    private Long reviewId;
}
