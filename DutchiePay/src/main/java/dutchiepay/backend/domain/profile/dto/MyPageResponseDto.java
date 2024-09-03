package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponseDto {
    private String address;
    private String detail;
    private String phone;
    private Long coupon;
    private Long order;
    private String email;

    public static MyPageResponseDto from(User user, Long couponCount, Long orderCount) {
        return MyPageResponseDto.builder()
                .address(user.getAddress())
                .detail(user.getDetail())
                .phone(user.getPhone())
                .coupon(couponCount)
                .order(orderCount)
                .email(user.getEmail())
                .build();
    }
}
