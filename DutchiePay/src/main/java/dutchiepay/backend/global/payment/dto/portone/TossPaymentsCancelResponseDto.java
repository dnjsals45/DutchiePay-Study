package dutchiepay.backend.global.payment.dto.portone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TossPaymentsCancelResponseDto {

    private InnerPaymentCancellation cancellation;
    @Getter
    @NoArgsConstructor
    public static class InnerPaymentCancellation {
        private String status;
        private BigDecimal totalAmount; // 취소 총 금액

    }
}
