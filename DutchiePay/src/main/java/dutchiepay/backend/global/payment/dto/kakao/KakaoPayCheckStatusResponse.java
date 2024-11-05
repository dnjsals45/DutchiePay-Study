package dutchiepay.backend.global.payment.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayCheckStatusResponse {
    private String tid;
    private String cid;
    private String status;
}
