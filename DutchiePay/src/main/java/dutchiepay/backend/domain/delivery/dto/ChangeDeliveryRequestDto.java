package dutchiepay.backend.domain.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeDeliveryRequestDto {
    private Long addressId;
    @NotBlank(message = "배송지 이름을 입력해주세요.")
    private String addressName;
    @NotBlank(message = "받는이를 입력해주세요.")
    private String name;
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{1,11}$", message = "전화번호는 1이상 11이하 숫자여야 합니다.")
    private String phone;
    @NotBlank(message = "주소를 입력해주세요.")
    private String address;
    private String detail;
    @NotBlank(message = "우편번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
    private String zipCode;
    @NotNull(message = "기본 배송지 여부를 입력해주세요.")
    private Boolean isDefault;
}

