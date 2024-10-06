package dutchiepay.backend.domain.delivery.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDelieveryRequestDto {
    private String addressName;
    private String name;
    private String phone;
    private String address;
    private String detail;
    private String zipCode;
    private Boolean isDefault;
}
