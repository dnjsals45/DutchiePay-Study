package dutchiepay.backend.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmPurchaseRequestDto {
    @NotBlank(message = "주문 ID를 입력해주세요.")
    private Long orderId;
}
