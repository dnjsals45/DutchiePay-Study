package dutchiepay.backend.global.payment.dto.portone;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TossPaymentsCancelResponseDto {

    private String status;
    private BigDecimal totalAmount; // 취소 총 금액
}
