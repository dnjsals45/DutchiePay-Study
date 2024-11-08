package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAskRequestDto {
    @NotNull(message = "공구게시글 ID를 입력해주세요.")
    private Long buyId;
    @NotBlank(message = "문의 내용을 입력해주세요.")
    private String content;
    @NotNull(message = "비밀글 여부를 입력해주세요.")
    private Boolean isSecret;
}
