package dutchiepay.backend.domain.user.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FindEmailResponseDto {
    private String email;

    public static FindEmailResponseDto of(String email) {
        return FindEmailResponseDto.builder()
                .email(email)
                .build();
    }
}
