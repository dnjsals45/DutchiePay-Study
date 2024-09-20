package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLocationRequestDto {
    private String location;
}
