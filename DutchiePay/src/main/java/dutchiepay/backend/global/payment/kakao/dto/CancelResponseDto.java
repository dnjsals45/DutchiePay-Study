package dutchiepay.backend.global.payment.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelResponseDto {
    private String tid;
    private String status;
}
