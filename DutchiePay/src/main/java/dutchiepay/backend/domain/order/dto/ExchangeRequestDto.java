package dutchiepay.backend.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRequestDto {
    @NotBlank(message = "교환 요청 타입을 입력해주세요.")
    private String type;
    @NotBlank(message = "주문 ID를 입력해주세요.")
    private Long orderId;
    @NotBlank(message = "교환 사유를 입력해주세요.")
    private String reason;
    private String detail;
}
