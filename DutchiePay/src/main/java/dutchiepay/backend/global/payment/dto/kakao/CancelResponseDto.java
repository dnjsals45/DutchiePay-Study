package dutchiepay.backend.global.payment.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelResponseDto {
    private String tid;
    private String status;
}
