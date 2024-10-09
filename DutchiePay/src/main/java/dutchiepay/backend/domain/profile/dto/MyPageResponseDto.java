package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponseDto {
    private String phone;
    private String email;

    public static MyPageResponseDto from(User user) {
        String maskingPhone = user.getPhone().replaceAll("(\\d{2,3})(\\d{3,4})(\\d{4})", "$1-****-$3");

        return MyPageResponseDto.builder()
                .phone(maskingPhone)
                .email(user.getEmail())
                .build();
    }

}
