package dutchiepay.backend.domain.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetPreSignedUrlRequestDto {
    @NotBlank(message = "파일 이름을 입력해주세요.")
    private String fileName;
}
