package dutchiepay.backend.domain.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteDeliveryRequestDto {
    @NotNull(message = "배송지번호를 입력해주세요.")
    private Long addressId;
}
