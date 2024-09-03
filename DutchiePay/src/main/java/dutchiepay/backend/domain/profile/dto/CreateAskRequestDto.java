package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAskRequestDto {
    private Long orderId;
    private String content;
    private Boolean isSecret;
}
