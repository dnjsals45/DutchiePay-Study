package dutchiepay.backend.domain.delivery.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteDeliveryRequestDto {
    private Long addressId;
}
