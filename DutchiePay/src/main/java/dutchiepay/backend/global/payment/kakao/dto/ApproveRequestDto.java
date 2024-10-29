package dutchiepay.backend.global.payment.kakao.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApproveRequestDto {
    private String tid;
    private String partnerOrderId;
    private String partnerUserId;
}
