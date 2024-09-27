package dutchiepay.backend.domain.profile.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAddressRequestDto {
    private String addressName;
    private String name;
    private String phone;
    private String address;
    private String detail;
    private String zipCode;
    private Boolean isDefault;
}
