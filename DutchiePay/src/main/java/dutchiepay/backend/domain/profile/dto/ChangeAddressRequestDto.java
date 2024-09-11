package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeAddressRequestDto {
    private String address;
    private String detail;
}
