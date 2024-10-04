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

        return MyPageResponseDto.builder()
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

}
