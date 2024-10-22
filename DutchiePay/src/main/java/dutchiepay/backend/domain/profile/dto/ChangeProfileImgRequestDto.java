package dutchiepay.backend.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeProfileImgRequestDto {
    private String profileImg;
}
