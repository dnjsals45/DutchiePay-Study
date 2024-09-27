package dutchiepay.backend.domain.profile.dto;

import dutchiepay.backend.entity.Address;
import dutchiepay.backend.entity.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponseDto {
    private List<UserAddress> deliveryAddress;
    private String phone;
    private Long coupon;
    private Long order;
    private String email;

    public static MyPageResponseDto from(User user, List<Address> addressList, Long couponCount, Long orderCount) {
        List<UserAddress> addressResponse = new ArrayList<>();

        for (Address a : addressList) {
            addressResponse.add(UserAddress.from(a));
        }

        return MyPageResponseDto.builder()
                .deliveryAddress(addressResponse)
                .phone(user.getPhone())
                .coupon(couponCount)
                .order(orderCount)
                .email(user.getEmail())
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserAddress {
        private Long addressId;
        private String addressName;
        private String name;
        private String phone;
        private String address;
        private String detail;
        private String zipCode;
        private boolean isDefault;

        public static UserAddress from(Address address) {
            return UserAddress.builder()
                    .addressId(address.getAddressId())
                    .addressName(address.getAddressName())
                    .name(address.getReceiver())
                    .phone(address.getPhone())
                    .address(address.getAddressInfo())
                    .detail(address.getDetail())
                    .zipCode(address.getZipCode())
                    .isDefault(address.getIsDefault())
                    .build();
        }
    }

}
