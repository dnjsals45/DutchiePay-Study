package dutchiepay.backend.domain.delivery.dto;

import dutchiepay.backend.entity.Address;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDeliveryResponseDto {
    private Long addressId;

    public static CreateDeliveryResponseDto from(Address newAddress) {
        return CreateDeliveryResponseDto.builder()
                .addressId(newAddress.getAddressId())
                .build();
    }
}
