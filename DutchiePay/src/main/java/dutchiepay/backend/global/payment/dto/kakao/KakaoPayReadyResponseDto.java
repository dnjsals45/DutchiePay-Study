package dutchiepay.backend.global.payment.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayReadyResponseDto {
    private String redirectUrl;

    public static KakaoPayReadyResponseDto from(String redirectUrl) {
        return new KakaoPayReadyResponseDto(redirectUrl);
    }
}
