package dutchiepay.backend.global.payment.dto.kakao;

import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadyRequestDto {
    private Long buyId;
    private String productName;
    @Max(value = 99, message = "최대 수량은 99개까지 가능합니다.")
    private Integer quantity;
    private Integer totalAmount;
    private Integer taxFreeAmount;
    private String receiver;
    private String phone;
    private String zipCode;
    private String address;
    private String detail;
    private String message;
}
