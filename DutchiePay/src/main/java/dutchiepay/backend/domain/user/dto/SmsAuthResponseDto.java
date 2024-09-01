package dutchiepay.backend.domain.user.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsAuthResponseDto {
    private String code;

    public static SmsAuthResponseDto of(String code) {
        return SmsAuthResponseDto.builder()
                .code(code)
                .build();
    }
}
