package dutchiepay.backend.global.payment.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayCheckStatusResponse {
    private String tid;
    private String cid;
    private String status;
}
