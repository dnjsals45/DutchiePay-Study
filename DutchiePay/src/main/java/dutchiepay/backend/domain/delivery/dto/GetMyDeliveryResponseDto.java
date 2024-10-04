package dutchiepay.backend.domain.delivery.dto;

import dutchiepay.backend.entity.Address;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyDeliveryResponseDto {
    private Long addressId;
    private String addressName;
    private String name;
    private String phone;
    private String address;
    private String detail;
    private String zipCode;
    private boolean isDefault;

    public static List<GetMyDeliveryResponseDto> from(List<Address> addressList) {
        List<GetMyDeliveryResponseDto> addressResponse = new ArrayList<>();

        for (Address address : addressList) {
            addressResponse.add(GetMyDeliveryResponseDto.builder()
                    .addressId(address.getAddressId())
                    .addressName(address.getAddressName())
                    .name(address.getReceiver())
                    .phone(address.getPhone())
                    .address(address.getAddressInfo())
                    .detail(address.getDetail())
                    .zipCode(address.getZipCode())
                    .isDefault(address.getIsDefault())
                    .build());
        }

        return addressResponse;
    }
}
